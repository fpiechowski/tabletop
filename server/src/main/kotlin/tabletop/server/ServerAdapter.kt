package tabletop.server

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.tomcat.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.error.CommonError
import tabletop.common.event.Event
import tabletop.common.server.Server
import tabletop.server.di.Dependencies

class ServerAdapter(
    private val dependencies: Dependencies
) : Server(), ConnectionCommunicator.Aware {
    private val logger = KotlinLogging.logger { }
    val application: CompletableDeferred<Application> = CompletableDeferred()


    fun launch() = either {
        catch({
            embeddedServer(Tomcat, port = 8080, host = "127.0.0.1") {
                module().also {
                    application.complete(this)
                }
            }.start(wait = true)
        }) {
            raise(Error("Error on starting server", CommonError.ThrowableError(it)))
        }
    }

    fun Application.module() = apply {
        launch {
            install(WebSockets)
            serve()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun serve() {
        application.await().routing {
            webSocket {
                val connection = Connection(this, TMVar.empty())
                val connectionScopeDependencies = dependencies.ConnectionScope(connection)

                with(connectionScopeDependencies) {
                    recover<CommonError, Unit>(
                        block = {
                            dependencies.state.connections.update { it + connection }

                            receiveIncomingCommands(connectionScopeDependencies) {
                                with(eventHandler) {
                                    it.handle().bind()
                                }
                            }
                        },
                        catch = {
                            with(connectionErrorHandler) { CommonError.ThrowableError(it).handle() }
                            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, it.message ?: "unknown error"))
                        },
                        recover = {
                            with(connectionErrorHandler) { it.handle() }
                            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, it.message ?: "unknown error"))
                        }
                    )
                }
            }
        }
    }

    private suspend fun receiveIncomingCommands(
        connectionScopeDependencies: Dependencies.ConnectionScope,
        onEach: suspend Raise<CommonError>.(Event) -> Unit
    ) = with(connectionScopeDependencies) {
        connectionCommunicator.receiveIncoming<Event>({
            onEach(it)
        }) {
            with(connectionErrorHandler) { it.handle() }
        }
    }

    companion object;

}
