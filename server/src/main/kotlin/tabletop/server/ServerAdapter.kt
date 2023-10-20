package tabletop.server

import arrow.core.raise.Raise
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import tabletop.common.auth.Authentication
import tabletop.common.command.Command
import tabletop.common.connection.Connection
import tabletop.common.connection.receiveFlow
import tabletop.common.connection.send
import tabletop.common.error.CommonError
import tabletop.common.error.handleConnection
import tabletop.common.error.handleTerminal
import tabletop.common.process.publish
import tabletop.common.process.startProcessing
import tabletop.common.serialization.Serialization
import tabletop.common.server.Server
import tabletop.common.transformFold
import tabletop.server.command.process
import tabletop.server.persistence.Persistence
import java.util.*

class ServerAdapter : Server() {
    val application: CompletableDeferred<Application> = CompletableDeferred()
    val connections: MutableSet<Connection> = Collections.synchronizedSet(LinkedHashSet())

    companion object
}

context (Persistence, Serialization, Authentication)
fun ServerAdapter.start() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        module().also {
            application.complete(this)
        }
    }.start(wait = true)
}

context (Persistence, ServerAdapter, Serialization, Authentication)
fun Application.module() = apply {
    launch {
        install(WebSockets)
        serve()
    }
}


context (Persistence, ServerAdapter, Serialization, Authentication)
private suspend fun ServerAdapter.serve() {
    application.await().routing {

        webSocket {
            val connection = Connection(this, TMVar.empty())
            connections += connection

            with(connection) {
                with(Command.Processor()) {
                    with(Command.Result.Processor()) {
                        recover<CommonError, Unit>(
                            block = {
                                launchCommandProcessing()

                                launchCommandResultProcessing(connection)

                                receiveIncomingCommands { it.publish() }
                            },
                            catch = {
                                CommonError.ThrowableError(it).handleConnection(ServerAdapter)
                            },
                            recover = {
                                it.handleConnection(ServerAdapter)
                                session.close(CloseReason(CloseReason.Codes.NORMAL, "Closing due to error $it"))
                            }
                        )
                    }
                }
            }
        }
    }
}

context (CoroutineScope, Command.Result.Processor, ServerAdapter, Connection, Serialization)
private fun launchCommandResultProcessing(
    connection: Connection
) {
    launch {
        startProcessing<Command.Result<*, *>> { result ->
            recover({
                val commandResult = (result as Command.Result<Command, Command.Result.Data>)
                if (result.shared) connections.forEach { with(it) { commandResult.send() } }
                else with(connection) { commandResult.send() }
            }) {
                it.handleTerminal(Command.Result.Processor)
            }
        }
    }
}

context (CoroutineScope, Command.Processor, Command.Result.Processor, ServerAdapter, Connection, Serialization, Persistence, Authentication)
private fun launchCommandProcessing() {
    launch {
        startProcessing<Command> {
            recover({
                it.process().publish()
            }) {
                it.handleConnection(Command.Processor)
            }
        }
    }
}

context (Serialization, Connection, ServerAdapter)
private suspend fun receiveIncomingCommands(onEach: suspend Raise<CommonError>.(Command) -> Unit) =
    receiveFlow<Command>()
        .transformFold { it.handleConnection(ServerAdapter) }
        .transform {
            recover({ onEach(it) }) {
                it.handleConnection(ServerAdapter)
            }
            emit(it)
        }
        .collect()


