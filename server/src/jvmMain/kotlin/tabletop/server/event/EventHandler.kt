package tabletop.server.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.update
import tabletop.server.di.ConnectionDependencies
import tabletop.server.di.Dependencies
import tabletop.shared.dnd5e.DnD5e
import tabletop.shared.error.CommonError
import tabletop.shared.error.ErrorHandler.Companion.use
import tabletop.shared.error.NotFoundError
import tabletop.shared.error.UnsupportedSubtypeError
import tabletop.shared.event.*
import tabletop.shared.game.Game
import tabletop.shared.plus
import tabletop.shared.scene.Scene
import tabletop.shared.scene.token.Tokenizable


@Suppress("UNCHECKED_CAST")
class EventHandler(
    private val dependencies: Dependencies,
    private val connectionDependencies: ConnectionDependencies,
) {
    private val persistence by lazy { dependencies.persistence }
    private val state by lazy { dependencies.state }
    private val authentication by lazy { dependencies.authentication }
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
                        is CharacterUpdateRequested -> handle().bind()
                        is SceneOpeningRequested -> SceneOpened(sceneId).handle().bind()
                        else -> raise(UnsupportedSubtypeError(RequestEvent::class))
                    }

                    is ResultEvent -> {
                        when (this@handle) {
                            is GameLoaded -> state.connectionToGame.update {
                                it.plus(connectionDependencies.connection to game)
                            }

                            else -> Unit
                        }

                        with(connectionDependencies) {
                            with(connectionCommunicator) {
                                connectionErrorHandler.use {
                                    if (this@handle.shared) {
                                        state.connectionToGame.value[connectionDependencies.connection]
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
                games.values.filter { it.gameMaster.user == user } +
                        games.values.filter { it.players.any { it.value.user == user } }
                            .toSet()
            }.bind()
                .toSet()
            GamesLoaded(games).handle().bind()
        }

    private suspend fun CharacterUpdateRequested.handle(): Either<CommonError, Unit> =
        either {
            val game = persistence.retrieve { games[gameId] }.bind()
                .let {
                    when (it.system) {
                        !is DnD5e -> raise(UnsupportedSubtypeError(Game::class))
                        else -> it as Game<DnD5e>
                    }
                }

            val updatedGame = (Game.system<DnD5e>() compose DnD5e.characters).modify(game) {
                it + character
            }

            with(persistence) {
                updatedGame.persist().bind()
            }

            CharacterUpdated(character).handle()
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

            val tokenizable = game.tokenizableEntities[tokenizableId]
                ?: raise(NotFoundError(Tokenizable::class, tokenizableId))
            val scene = game.scenes[sceneId] ?: raise(NotFoundError(Scene::class, sceneId))
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
