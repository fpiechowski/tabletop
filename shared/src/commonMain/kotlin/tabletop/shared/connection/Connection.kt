package tabletop.shared.connection

import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.fold
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Identifiable
import tabletop.shared.error.CommonError
import tabletop.shared.serialization.Serialization

class Connection(
    val host: String,
    val port: Int,
    val session: WebSocketSession,
    override val id: UUID = UUID.generateUUID()
) : Identifiable<UUID> {
    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

class ConnectionCommunicator(val connection: Connection, val serialization: Serialization) {

    val logger = KotlinLogging.logger {}

    suspend inline fun <reified T : Any> T.send() =
        send<T>(connection)

    suspend inline fun <reified T : Any> T.send(connections: Iterable<Connection>) =
        connections.map {
            send<T>(it)
        }

    suspend inline fun <reified T : Any> T.send(connection: Connection) =
        either {
            fold<CommonError, Unit, Unit>(
                block = {
                    connection.session.send(
                        with(serialization) { this@send.serialize<T>().bind() }
                    )
                },
                catch = {
                    raise(
                        Connection.Error(
                            "Error on sending ${this@send}",
                            CommonError.ThrowableError(it)
                        )
                    )
                },
                recover = { raise(Connection.Error("Error on sending ${this@send}", it)) },
                transform = { logger.debug { "Sent ${this@send}" } }
            )
        }

    suspend inline fun <reified T : Any> receiveIncoming(
        onEach: Raise<CommonError>.(T) -> Unit,
        onError: (CommonError) -> Unit
    ) {

        for (frame in connection.session.incoming) {
            either {
                val frameText = (frame as Frame.Text).readText()

                recover<CommonError, T>(
                    block = {
                        with(serialization) { frameText.deserialize<T>().bind() }
                    },
                    recover = { error ->
                        if (error is Serialization.Error) {
                            val incomingError = recover({
                                with(serialization) { frameText.deserialize<CommonError>().bind() }
                            }) {
                                raise(Connection.Error("Unhandled incoming message", it))
                            }

                            raise(incomingError)
                        } else raise(Connection.Error("Error on receive", error))
                    }
                ).let { onEach(it) }
            }.onLeft(onError)
        }
    }
}