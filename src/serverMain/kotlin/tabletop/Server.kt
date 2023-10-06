package tabletop

import arrow.core.raise.either
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.encodeToString
import tabletop.auth.Authentication
import tabletop.auth.Credentials
import tabletop.auth.authenticate
import tabletop.command.Command
import tabletop.persistence.Persistence
import tabletop.serialization.Serialization
import java.util.*

fun Application.commandHandler() {
    with(Persistence) {
        with(Serialization) {
            with(Authentication) {
                routing {
                    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

                    webSocket {
                        connections += Connection(this)

                        val credentials = json.decodeFromString<Credentials<*, *>>((incoming.receive() as Frame.Text)
                            .readText())

                        either {
                            credentials.authenticate()

                            processCommands(incoming, connections)
                        }.onLeft {
                            outgoing.send(Frame.Text(json.encodeToString(ExceptionTextFrame.from(it))))
                            close()
                        }
                    }
                }

            }
        }
    }
}

context (Serialization)
private suspend fun processCommands(
    receiveChannel: ReceiveChannel<Frame>,
    connections: MutableSet<Connection>
) {
    for (frame in receiveChannel) {
        val command = json.decodeFromString<Command>((frame as Frame.Text).readText())
        //command.execute()
        connections.forEach {
            it.session.send(frame)
        }
    }
}
