package tabletop.server.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.update
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler.Companion.use
import tabletop.common.error.UnsupportedSubtypeError
import tabletop.common.event.*
import tabletop.common.game.Game
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
            val games = persistence.retrieve {
                games.values.filter { it.gameMaster.user.id == userId } +
                        games.values.filter { it.players.any { it.user.id == userId } }
                            .toSet()
            }.bind()
            val listing = Game.Listing(games.map { Game.Listing.Item(it) })
            GameListingLoaded(listing).handle().bind()
        }

    private suspend fun GameLoadingRequested.handle(): Either<CommonError, Unit> =
        either {
            val game = ensureNotNull(persistence.retrieve { games[gameListingItem.id] }.bind()) {
                Event.Error("Game not found for ID $gameListingItem.id", null)
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
            val tokenizable = persistence.retrieve { games[gameId]?.tokenizables?.get(tokenizableId) }.bind()
                .let { ensureNotNull(it) { Event.Error("Tokenizable with ID $tokenizableId not found", null) } }

            val scene = persistence.retrieve { games[gameId]?.scenes?.get(sceneId) }.bind()
                .let { ensureNotNull(it) { Event.Error("Scene with ID $sceneId not found", null) } }

            val token = tokenizable.tokenize(scene, position)
                .also {
                    scene.tokens[it.id] = it
                }

            with(persistence) {
                scene.persist().bind()
            }

            TokenPlaced(token).handle()
        }
}
