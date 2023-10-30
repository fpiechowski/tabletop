package tabletop.client.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.future.await
import tabletop.client.di.Dependencies
import tabletop.client.server.ServerAdapter
import tabletop.common.command.Command
import tabletop.common.error.CommonError
import korlibs.korge.scene.Scene as UIScene

class EventHandler(
    private val dependencies: Dependencies
) {
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }
    private val eventHandler by lazy { dependencies.eventHandler }
    private val userInterface by lazy { dependencies.userInterface }

    private val logger = KotlinLogging.logger {}

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    suspend fun <T : Event> T.handle(): Either<Event.Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover<CommonError, Unit>({
                when (this@handle) {
                    is ConnectionAttempted ->
                        ServerAdapter(dependencies).connect(host, port, credentialsData).bind()

                    else -> {}
                }
            }) {
                raise(Event.Error("Error when handling $this", it))
            }
        }

    suspend fun <T : UIEvent<*>> T.handle(uiScene: UIScene): Either<Event.Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover<CommonError, Unit>({
                uiScene.sceneView.dispatch(this@handle)
            }) {
                raise(Event.Error("Error when handling $this", it))
            }
        }

    suspend fun <T : ConnectionEvent> T.handle(connectionScope: Dependencies.ConnectionScope): Either<Event.Error, Unit> =
        with(connectionScope) {
            either {
                logger.debug { "Handling ${this@handle}" }

                recover<CommonError, Unit>({
                    when (this@handle) {
                        is UserAuthenticated -> {
                            userInterface.connectionScene.connectionWindow.await().closeAnimated()

                            with(connectionCommunicator) {
                                (Command.GetGames(user.id) as Command)
                                    .send()
                                    .bind()
                            }
                        }

                        is LoadingGameAttempted ->
                            with(connectionCommunicator) {
                                (Command.GetGame(gameListingItem.id) as Command)
                                    .send()
                                    .bind()
                            }

                        else -> {}
                    }
                }) {
                    raise(Event.Error("Error when handling $this", it))
                }
            }
        }
}