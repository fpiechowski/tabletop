package tabletop.client.event

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import arrow.optics.copy
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.error.UIErrorHandler
import tabletop.client.game.GameScreen
import tabletop.client.navigation.Navigation
import tabletop.client.server.ServerAdapter
import tabletop.client.state.State
import tabletop.common.error.CommonError
import tabletop.common.error.NotFoundError
import tabletop.common.error.UnsupportedSubtypeError
import tabletop.common.event.*
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.scene.tokens
import kotlin.coroutines.CoroutineContext

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class EventHandler(
    private val dependencies: Dependencies,
) : CoroutineScope {
    private val logger = KotlinLogging.logger {}

    private val state: State = dependencies.state
    private val uiErrorHandler: UIErrorHandler = dependencies.uiErrorHandler
    private suspend fun navigation(): Navigation = dependencies.navigation.await()

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
        }


    private fun ConnectionAttempted.handle(): Either<CommonError, Unit> =
        either {
            state.connectionJob.value?.cancel(CancellationException("New Connection Attempted"))
            state.connectionJob.value = Job()
            state.connectionJob.value?.let {
                launch(it) {
                    recover({
                        ServerAdapter(dependencies, this@EventHandler, uiErrorHandler)
                            .connect(host, port, credentialsData).bind()
                    }) {
                        ConnectionEnded(it).handle().bind()
                        with(uiErrorHandler) { it.handle() }
                    }
                }
            }
        }

    private suspend fun ConnectionEnded.handle(): Either<CommonError, Unit> =
        either {
            dependencies.state.maybeUser.value = null
            dependencies.state.maybeGame.value = null
            navigation().popUntil { it is ConnectionScreen }
        }

    private suspend fun <T : ResultEvent> T.handle(): Either<CommonError, Unit> =
        either {
            dependencies.state.connectionDependencies.value?.run {
                when (this@handle) {
                    is GameLoaded -> {
                        state.maybeGame.value = this@handle.game

                        navigation().push(GameScreen(dependencies))
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
                                Game.scenes.bind() set game.scenes + (scene.id to scene)
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

                    else -> raise(UnsupportedSubtypeError(ResultEvent::class))
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

