package tabletop.client.server

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import tabletop.client.command.execute
import tabletop.client.error.handleUI
import tabletop.client.event.Event
import tabletop.client.input.Input
import tabletop.client.server.ServerAdapter.Companion.httpClient
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.auth.Credentials
import tabletop.common.command.Command
import tabletop.common.connection.Connection
import tabletop.common.connection.receiveFlow
import tabletop.common.connection.send
import tabletop.common.error.CommonError
import tabletop.common.error.handleConnection
import tabletop.common.process.publish
import tabletop.common.process.startProcessing
import tabletop.common.serialization.Serialization
import tabletop.common.server.Server

class ServerAdapter(val host: String, val port: Int) : Server() {
    companion object {
        val httpClient: HttpClient = HttpClient {
            install(WebSockets)
        }
    }
}


context (Raise<Server.Error>, Serialization, Input, UserInterface, Event.Processor, State)
suspend fun ServerAdapter.connect(
    credentials: Credentials.UsernamePassword.Data,
) =
    catch({
        httpClient
            .webSocket(host = host, port = port) {
                with(Connection(this, TMVar.empty())) {
                    with(Command.Processor()) {
                        with(Command.Result.Processor()) {
                            recover({
                                KtxAsync.launch {
                                    startProcessing<Command.Result<*, *>> {
                                        recover({ it.execute() }) {
                                            Server.Error("Error on executing $it").handleConnection(Server)
                                        }
                                    }
                                }

                                KtxAsync.launch {
                                    startProcessing<Command> {
                                        recover({ it.send() }) {
                                            Server.Error("Error on sending $it to server").handleConnection(Server)
                                        }
                                    }
                                }

                                Command.SignIn(credentials.username, credentials.password).publish()

                                receiveIncomingCommandResults { it.publish() }
                            }) {
                                close(CloseReason(CloseReason.Codes.NORMAL, "Bye bye"))
                                raise(Server.Error("", it))
                            }
                        }
                    }
                }
            }
    }) { raise(Server.Error("Error on connecting to TabletopServer", CommonError.ThrowableError(it))) }

context (Serialization, Connection, ServerAdapter, UserInterface)
private suspend fun receiveIncomingCommandResults(
    onEach: suspend Raise<CommonError>.(Command.Result<Command, Command.Result.Data>) -> Unit
) =
    receiveFlow<Command.Result<Command, Command.Result.Data>>()
        .transform {
            recover({ onEach(it) }) {
                it.handleUI(ServerAdapter)
            }
            emit(it)
        }.collect()


