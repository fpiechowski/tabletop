package tabletop.common.connection

import arrow.core.raise.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Identifiable
import tabletop.common.error.CommonError
import tabletop.common.error.handleTerminal
import tabletop.common.logging.logger
import tabletop.common.serialization.Serialization
import tabletop.common.serialization.deserialize
import tabletop.common.serialization.serialize
import tabletop.common.transformFold

class Connection(val session: WebSocketSession, override val id: UUID = UUID.generateUUID()) : Identifiable<UUID> {

    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}

context (Raise<Connection.Error>, Connection, Serialization)
suspend inline fun <reified T : Any> T.send(
): Unit = fold(
    block = {
        session.send(
            Frame.Text(
                this@send.serialize().also { Connection.logger.debug { "Outgoing payload: $it" } })
        )
    },
    catch = { raise(Connection.Error("Error on sending ${this@send}", CommonError.ThrowableError(it))) },
    recover = { raise(Connection.Error("Error on sending ${this@send}", it)) },
    transform = { it.also { Connection.logger.debug { "Sent ${this@send}" } } }
)

context (Raise<Connection.Error>, Connection, Serialization)
suspend inline fun <reified T : Any> receive(): T {
    val frameText = catch({ (session.incoming.receive() as Frame.Text).readText() }) {
        raise(Connection.Error("Can't receive incoming frame", CommonError.ThrowableError(it)))
    }.also { Connection.logger.debug { "Incoming payload: $it" } }

    return fold<CommonError, T, T>(
        block = { deserialize<T>(frameText) },
        catch = { raise(Connection.Error("Error on receive", CommonError.ThrowableError(it))) },
        recover = {
            raise(
                recover({
                    Connection.Error(
                        "Received error response",
                        deserialize<CommonError>(frameText)
                    )
                }) {
                    raise(
                        Connection.Error(
                            "Received content is not ${T::class.simpleName} nor ${CommonError::class.simpleName}",
                            it
                        )
                    )
                })
        },
        transform = { it.also { Connection.logger.debug { "Received $it" } } }
    )
}

context (Connection, Serialization)
inline fun <reified T : Any> receiveFlow(
): Flow<T> =
    session.incoming.receiveAsFlow()
        .map { frame ->
            either {
                fold<CommonError, T, T>(
                    block = {
                        deserialize((frame as Frame.Text).readText()
                            .also { Connection.logger.debug { "Incoming payload: $it" } })
                    },
                    catch = { raise(Connection.Error("Error on receive", CommonError.ThrowableError(it))) },
                    recover = { raise(Connection.Error("Error on receive", it)) },
                    transform = { it.also { Connection.logger.debug { "Received $it" } } }
                )
            }.onLeft {
                it.handleTerminal(Connection)
            }
        }.transformFold { Connection.logger.debug { "Skippable frame received for ${T::class.qualifiedName} flow" } }
