package tabletop.common.connection

import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.fold
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.*
import tabletop.common.Identifiable
import tabletop.common.di.CommonDependencies
import tabletop.common.error.CommonError
import tabletop.common.serialization.Serialization
import tabletop.common.user.User
import java.util.*

class Connection(
    val session: WebSocketSession,
    val authenticatedUser: TMVar<User>,
    override val id: UUID = UUID.randomUUID()
) : Identifiable<UUID> {
    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError(), java.io.Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}

class ConnectionCommunicator(val dependencies: CommonDependencies.ConnectionScope) {
    val logger = KotlinLogging.logger { }

    interface Aware

    suspend inline fun <reified T : Any> T.send() =
        with(dependencies) {
            send<T>(connection)
        }

    suspend inline fun <reified T : Any> T.send(connections: Iterable<Connection>) =
        connections.map {
            send<T>(it)
        }

    suspend inline fun <reified T : Any> T.send(connection: Connection) =
        with(dependencies) {
            either {
                fold<CommonError, Unit, Unit>(
                    block = {
                        connection.session.send(
                            with(serialization) { this@send.serialize<T>().bind() }
                                .also { logger.debug { "Outgoing payload: $it" } }
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
                    transform = { it.also { logger.debug { "Sent ${this@send}" } } }
                )
            }
        }

    suspend inline fun <reified T : Any> receiveIncoming(
        onEach: Raise<CommonError>.(T) -> Unit,
        onError: (CommonError) -> Unit
    ): Unit =
        with(dependencies) {
            logger.debug { "Started receiving through WebSocket ${T::class}" }

            for (frame in connection.session.incoming) {
                either {
                    val frameBytes = (frame as Frame.Binary).readBytes()
                        .also { logger.debug { "Incoming payload: $it" } }

                    recover<CommonError, T>(
                        block = {
                            with(serialization) { frameBytes.deserialize<T>().bind() }
                        },
                        recover = { error ->
                            if (error is Serialization.Error) {
                                val incomingError = recover({
                                    logger.warn { "Failed to deserialize ${T::class}, trying to deserialize ${CommonError::class}" }
                                    with(serialization) { frameBytes.deserialize<CommonError>().bind() }
                                }) {
                                    raise(Connection.Error("Unhandled incoming message", it))
                                }

                                raise(incomingError)
                            } else raise(Connection.Error("Error on receive", error))
                        }
                    ).also { logger.debug { "Received $it" } }
                        .let { onEach(it) }
                }.onLeft(onError)
            }
        }

}