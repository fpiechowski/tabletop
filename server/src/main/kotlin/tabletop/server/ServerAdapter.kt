package tabletop.server

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import tabletop.common.command.Command
import tabletop.common.command.CommandResult
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.handleErrors
import tabletop.common.server.Server
import tabletop.server.di.DependenciesAdapter
import java.util.*

class ServerAdapter(
    private val dependencies: DependenciesAdapter
) : Server() {
    val application: CompletableDeferred<Application> = CompletableDeferred()
    val connections: MutableSet<Connection> = Collections.synchronizedSet(mutableSetOf())

    fun launch() = either {
        catch({
            embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
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
                connections += connection

                with(connectionScopeDependencies) {
                    recover<CommonError, Unit>(
                        block = {
                            receiveIncomingCommands(connectionScopeDependencies) {
                                with(commandExecutor) {
                                    with(connectionCommunicator) {
                                        (it.execute().bind() as CommandResult).send().bind()
                                    }
                                }
                            }
                        },
                        catch = {
                            with(connectionErrorHandler) { CommonError.ThrowableError(it).handle() }
                        },
                        recover = {
                            with(connectionErrorHandler) { it.handle() }
                            //connection.session.close(CloseReason(CloseReason.Codes.NORMAL, "Closing due to error $it"))
                        }
                    )
                }
            }
        }
    }

    private suspend fun receiveIncomingCommands(
        connectionScopeDependencies: DependenciesAdapter.ConnectionScope,
        onEach: suspend Raise<CommonError>.(Command) -> Unit
    ) = with(connectionScopeDependencies) {
        connectionCommunicator.receiveIncomingAsEithersFlow<Command>()
            .handleErrors(connectionErrorHandler)
            .transform {
                recover({ onEach(it) }) {
                    with(connectionErrorHandler) { it.handle() }
                }
                emit(it)
            }
            .collect()
    }

    companion object;

}
