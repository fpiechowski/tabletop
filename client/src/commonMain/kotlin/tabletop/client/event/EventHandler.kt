package tabletop.client.event

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.io.async.launch
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import kotlinx.coroutines.Job
import tabletop.client.di.Dependencies
import tabletop.client.server.ServerAdapter
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.error.CommonError
import tabletop.common.error.NotFoundError
import tabletop.common.error.UnsupportedSubtypeError
import tabletop.common.event.*
import tabletop.common.scene.Scene

@KorgeInternal
@KorgeExperimental
class EventHandler(
    private val dependencies: Dependencies
) : ConnectionCommunicator.Aware {
    private val logger = KotlinLogging.logger {}

    private val userInterface by lazy { dependencies.userInterface }
    private val state by lazy { dependencies.state }
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    suspend fun <T : Event> T.handle(): Either<Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover<CommonError, Unit>({
                when (this@handle) {
                    is RequestEvent -> sendToServer().bind()
                    is ResultEvent -> handle().bind()
                    is UIEvent<*, *> -> {
                        dispatchToUI().bind()
                    }

                    is ConnectionAttempted -> {
                        val connectionJob = Job()
                        launch(connectionJob) {
                            recover({ ServerAdapter(dependencies).connect(host, port, credentialsData).bind() }) {
                                with(uiErrorHandler) { it.handle() }
                            }
                        }
                        state.connectionJob.value = connectionJob
                    }

                    else -> logger.warn { "Unhandled event ${this@handle}" }
                }
            }) {
                raise(Error("Error when handling ${this@handle}", it))
            }
        }

    private suspend fun <T : ResultEvent> T.handle(): Either<CommonError, Unit> =
        either {
            dependencies.connectionScope.value?.run {
                with(connectionCommunicator) {
                    when (this@handle) {
                        is GameLoaded -> {
                            state.game.value = this@handle.game

                            GameLoadedUIEvent(this@handle).dispatchToUI().bind()

                            with(userInterface) {
                                sceneContainer.await().changeTo { gameScene }
                            }
                        }

                        is GameListingLoaded -> {
                            state.gameListing.value = this@handle.listing
                            GameListingLoadedUIEvent(this@handle).dispatchToUI().bind()
                        }

                        is UserAuthenticated -> {
                            state.user.value = this@handle.user
                            UserAuthenticatedUIEvent(this@handle).dispatchToUI().bind()
                            GameListingRequested(user.id)
                                .send()
                                .bind()
                        }

                        is TokenPlaced -> {
                            state.game.value?.scenes?.get(token.scene.id)?.tokens?.set(token.id, token)
                            TokenPlacedUIEvent(this@handle).dispatchToUI().bind()
                        }

                        is SceneOpened -> {
                            val scene =
                                ensureNotNull(state.game.value?.scenes?.get(sceneId)) {
                                    NotFoundError(
                                        Scene::class,
                                        sceneId
                                    )
                                }
                            state.currentScene.value = scene
                            SceneOpenedUIEvent(this@handle, scene).dispatchToUI().bind()
                        }

                        else -> raise(UnsupportedSubtypeError(ResultEvent::class))
                    }
                }
            }

        }

    private suspend inline fun <reified T : RequestEvent> T.sendToServer(): Either<CommonError, Unit> =
        either {
            dependencies.connectionScope.value?.run {
                with(connectionCommunicator) {
                    this@sendToServer.send().bind()
                }
            }
        }

    private suspend fun <T : UIEvent<*, *>> T.dispatchToUI(): Either<CommonError, Unit> =
        either {
            logger.debug { "Handling ${this@dispatchToUI}" }

            catch({
                userInterface.stage.await().dispatch(this@dispatchToUI)
            }) {
                raise(CommonError.ThrowableError(it))
            }
        }
}