package tabletop.client.event

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import arrow.optics.copy
import com.arkivanov.decompose.value.update
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.update
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.error.ErrorDialogs
import tabletop.client.game.GameScreen
import tabletop.client.navigation.Navigation
import tabletop.client.server.Server
import tabletop.client.state.State
import tabletop.shared.dnd5e.DnD5e
import tabletop.shared.error.CommonError
import tabletop.shared.error.NotFoundError
import tabletop.shared.error.UnsupportedSubtypeError
import tabletop.shared.event.*
import tabletop.shared.game.Game
import tabletop.shared.scene.Scene
import kotlin.coroutines.CoroutineContext

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class EventHandler(
    private val dependencies: Dependencies,
) : CoroutineScope {
    private val logger = KotlinLogging.logger {}

    private val state: State = dependencies.state
    private val errorDialogs: ErrorDialogs = dependencies.errorDialogs

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    suspend fun <T : Event> T.handle(): Either<Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover({
                when (this@handle) {
                    is RequestEvent -> sendToServer().bind()
                    is ResultEvent -> handle().bind()
                    is ConnectionAttempted -> handle().bind()
                    is ConnectionEnded -> handle().bind()
                    else -> logger.warn { "Unhandled event ${this@handle}" }
                }
            }) {
                raise(Error("Error when handling ${this@handle}", it))
            }

            logger.debug { "Handled ${this@handle}" }
        }


    private fun ConnectionAttempted.handle(): Either<CommonError, Unit> =
        either {
            state.connectionJob.value?.cancel(CancellationException("New Connection Attempted"))
            state.connectionJob.value = Job()
            state.connectionJob.value?.let {
                launch(it) {
                    recover({
                        Server(dependencies, this@EventHandler, errorDialogs)
                            .connect(host, port, credentialsData).bind()
                    }) {
                        ConnectionEnded(it).handle().bind()
                        with(errorDialogs) { it.handle() }
                    }
                }
            }
        }

    private fun ConnectionEnded.handle(): Either<CommonError, Unit> =
        either {
            dependencies.state.maybeUser.value = null
            dependencies.state.maybeGame.value = null
            dependencies.navigation.currentScreen.update { Navigation.Screen.Connection }
        }

    private suspend fun <T : ResultEvent> T.handle(): Either<CommonError, Unit> =
        either {
            dependencies.state.connectionDependencies.value?.run {
                when (this@handle) {
                    is GameLoaded -> {
                        state.maybeGame.value = this@handle.game

                        dependencies.navigation.currentScreen.update { Navigation.Screen.Game }
                    }

                    is GamesLoaded -> {
                        state.games.value = this@handle.games
                    }

                    is UserAuthenticated -> {
                        state.maybeUser.value = this@handle.user
                        GameListingRequested(user.id)
                            .handle()
                            .bind()
                    }

                    is TokenPlaced -> {
                        with(state) {
                            val game = game.bind()
                            val scene = ensureNotNull(game.scenes[token.sceneId]) {
                                NotFoundError(
                                    Scene::class,
                                    token.sceneId
                                )
                            }.let {
                                it.copy {
                                    Scene.tokens set it.tokens + (token.id to token)
                                }
                            }

                            game.copy {
                                Game.scenes set game.scenes + (scene.id to scene)
                            }

                            maybeGame.value = game
                            state.scene.current.value = scene
                        }
                    }

                    is SceneOpened -> {
                        with(state) {
                            val scene =
                                ensureNotNull(game.bind().scenes[sceneId]) { NotFoundError(Scene::class, sceneId) }

                            state.scene.current.value = scene
                        }
                    }

                    is CharacterUpdated -> {
                        state.maybeGame.update {
                            it?.let { game ->
                                when (it.system) {
                                    is DnD5e -> (Game.system<DnD5e>() compose DnD5e.characters).modify(game as Game<DnD5e>) { it + (character.id to character) }
                                    else -> raise(UnsupportedSubtypeError(Game::class))
                                }
                            }
                        }
                    }
                }
            }
        }

    private suspend inline fun <reified T : RequestEvent> T.sendToServer(): Either<CommonError, Unit> =
        either {
            ensureNotNull(dependencies.state.connectionDependencies.value) {
                Error("Connection dependencies are not initialized", null)
            }.run {
                with(connectionCommunicator) {
                    this@sendToServer.send().bind()
                }
            }
        }

    override val coroutineContext: CoroutineContext = Dispatchers.Default
}
