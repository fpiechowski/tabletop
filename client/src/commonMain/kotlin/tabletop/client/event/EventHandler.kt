package tabletop.client.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.client.di.DependenciesAdapter
import tabletop.client.server.ServerAdapter
import tabletop.common.command.Command
import tabletop.common.error.CommonError

class EventHandler(
    private val dependencies: DependenciesAdapter
) {
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }
    private val eventHandler by lazy { dependencies.eventHandler }

    private val logger = KotlinLogging.logger {}

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    suspend fun <T : Event> T.handle(): Either<Event.Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover<CommonError, Unit>({
                when (this@handle) {
                    is ConnectionAttempted ->
                        recover({
                            ServerAdapter(dependencies).connect(host, port, credentialsData).bind()
                        }) {
                            with(uiErrorHandler) { it.handle() }
                        }

                    else -> {}
                }
            }) {
                raise(Event.Error("Error when handling $this", it))
            }
        }

    suspend fun <T : ConnectionEvent> T.handle(connectionScope: DependenciesAdapter.ConnectionScope): Either<Event.Error, Unit> =
        with(connectionScope) {
            either {
                logger.debug { "Handling ${this@handle}" }

                recover<CommonError, Unit>({
                    when (this@handle) {
                        is UserAuthenticated ->
                            with(connectionCommunicator) {
                                (Command.GetGames(user.id) as Command)
                                    .send()
                                    .bind()
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