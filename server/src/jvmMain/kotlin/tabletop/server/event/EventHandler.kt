package tabletop.server.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.update
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler.Companion.use
import tabletop.common.error.NotFoundError
import tabletop.common.error.UnsupportedSubtypeError
import tabletop.common.event.*
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Tokenizable
import tabletop.server.di.Dependencies


class EventHandler(
    private val connectionScopeDependencies: Dependencies.ConnectionScope,
) {
    private val persistence by lazy { connectionScopeDependencies.persistence }
    private val authentication by lazy { connectionScopeDependencies.authentication }
    private val state by lazy { connectionScopeDependencies.state }
    private val logger = KotlinLogging.logger { }
    suspend fun Event.handle(): Either<Event.Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }
            recover({
                when (this@handle) {
                    is RequestEvent -> when (this@handle) {
                        is AuthenticationRequested -> handle().bind()
                        is GameLoadingRequested -> handle().bind()
                        is GameListingRequested -> handle().bind()
                        is TokenPlacingRequested -> handle().bind()
                        is SceneOpeningRequested -> SceneOpened(sceneId).handle().bind()
                        else -> raise(UnsupportedSubtypeError(RequestEvent::class))
                    }

                    is ResultEvent -> {
                        when (this@handle) {
                            is GameLoaded -> state.connectionToGame.update {
                                it.plus(connectionScopeDependencies.connection to game)
                            }

                            else -> Unit
                        }

                        with(connectionScopeDependencies) {
                            with(connectionCommunicator) {
                                connectionErrorHandler.use {
                                    if (shared) {
                                        state.connectionToGame.value[connectionScopeDependencies.connection]
                                            ?.let { game ->
                                                state.connectionToGame.value
                                                    .filterValues { it == game }
                                                    .keys
                                                    .let { connections -> this@handle.send(connections) }
                                                    .forEach { connectionErrorHandler.use { it.bind() } }
                                            }
                                    } else {
                                        this@handle.send().bind()
                                    }
                                }
                            }
                        }
                    }

                    else -> raise(Event.Error("Unhandled Event type ${this::class.simpleName}", null))
                }
            }) {
                raise(Event.Error("Error on executing ${this@handle}", it))
            }.also { logger.debug { "Executed ${this@handle}" } }
        }

    private suspend fun GameListingRequested.handle(): Either<CommonError, Unit> =
        either {
            val user = persistence.retrieve { users[userId] }.bind()

            val games = persistence.retrieve {
                games.values.filter { it.gameMaster?.user == user } +
                        games.values.filter { it.players.any { it.user == user } }
                            .toSet()
            }.bind()
                .toSet()
            GamesLoaded(games).handle().bind()
        }

    private suspend fun GameLoadingRequested.handle(): Either<CommonError, Unit> =
        either {
            val game = ensureNotNull(persistence.retrieve { games[gameId] }.bind()) {
                Event.Error("Game not found for ID $gameId.id", null)
            }

            GameLoaded(game).handle().bind()
        }

    private suspend fun AuthenticationRequested.handle(): Either<CommonError, Unit> =
        either {
            val user = authentication.authenticate(credentialsData.username, credentialsData.password).bind()
            UserAuthenticated(user).handle().bind()
        }

    private suspend fun TokenPlacingRequested.handle(): Either<CommonError, Unit> =
        either {
            val game = persistence.retrieve { games[gameId] }.bind()

            val tokenizable = game.tokenizables.find { it.id == tokenizableId } ?: raise(NotFoundError(Tokenizable::class, tokenizableId))

            val scene = game.scenes.find { it.id == sceneId } ?: raise(NotFoundError(Scene::class, sceneId))

            val token = tokenizable.tokenize(scene, position)
                .also {
                    persistence
                }

            with(persistence) {
                scene.persist().bind()
            }

            TokenPlaced(token).handle()
        }
}
