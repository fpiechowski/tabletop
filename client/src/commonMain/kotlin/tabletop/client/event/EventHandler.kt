package tabletop.client.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import tabletop.client.di.Dependencies
import tabletop.client.error.UIErrorHandler
import tabletop.client.idLensOf
import tabletop.client.server.ServerAdapter
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.client.update
import tabletop.common.error.CommonError
import tabletop.common.error.UnsupportedSubtypeError
import tabletop.common.event.*
import kotlin.coroutines.CoroutineContext

class EventHandler(
    private val dependencies: Dependencies,
    private val userInterface: UserInterface,
    private val state: State,
    private val uiErrorHandler: UIErrorHandler
) : CoroutineScope {
    private val logger = KotlinLogging.logger {}


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

    private fun ConnectionEnded.handle(): Either<CommonError, Unit> =
        either {
            dependencies.router.navTo("connection")
        }

    private suspend fun <T : ResultEvent> T.handle(): Either<CommonError, Unit> =
        either {
            dependencies.state.connectionScope.value?.run {
                with(connectionCommunicator) {
                    when (this@handle) {
                        is GameLoaded -> {
                            state.game.bind().update(this@handle.game)

                            dependencies.router.navTo("game")
                        }

                        is GamesLoaded -> {
                            state.games.update(this@handle.games)
                        }

                        is UserAuthenticated -> {
                            state.user.update(this@handle.user)
                            GameListingRequested(user.id)
                                .handle()
                                .bind()
                        }

                        is TokenPlaced -> {
                            with(state) {
                                game.bind()
                                    .scene(token.sceneId).bind()
                                    .tokens.bind()
                                    .update { tokens -> tokens + token }
                            }
                        }


                        is SceneOpened -> {
                            with(state) {
                                currentScene.update {
                                    game.bind()
                                        .scene(sceneId).bind()
                                        .current
                                }
                            }
                        }

                        else -> raise(UnsupportedSubtypeError(ResultEvent::class))
                    }
                }
            }

        }


    private suspend inline fun <reified T : RequestEvent> T.sendToServer(): Either<CommonError, Unit> =
        either {
            dependencies.state.connectionScope.value?.run {
                with(connectionCommunicator) {
                    this@sendToServer.send().bind()
                }
            }
        }


    override val coroutineContext: CoroutineContext = Dispatchers.Default
}

