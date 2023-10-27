package tabletop.common.connection

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.fold
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Identifiable
import tabletop.common.di.CommonDependencies
import tabletop.common.error.CommonError
import tabletop.common.serialization.Serialization
import tabletop.common.user.User

class Connection(
    val session: WebSocketSession,
    val authenticatedUser: TMVar<User>,
    override val id: UUID = UUID.generateUUID()
) : Identifiable<UUID> {
    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}

class ConnectionCommunicator(val dependencies: CommonDependencies.ConnectionScope) {
    val logger = KotlinLogging.logger { }

    suspend inline fun <reified T : Any> T.send() = with(dependencies) {
        either {
            fold<CommonError, Unit, Unit>(
                block = {
                    connection.session.send(
                        Frame.Text(
                            with(serialization) { this@send.serialize().bind() }
                                .also { logger.debug { "Outgoing payload: $it" } })
                    )
                },
                catch = { raise(Connection.Error("Error on sending ${this@send}", CommonError.ThrowableError(it))) },
                recover = { raise(Connection.Error("Error on sending ${this@send}", it)) },
                transform = { it.also { logger.debug { "Sent ${this@send}" } } }
            )
        }
    }

    inline fun <reified T : Any> receiveIncomingAsEithersFlow(): Flow<Either<CommonError, T>> =
        with(dependencies) {
            connection.session.incoming.receiveAsFlow()
                .also { logger.debug { "Started receiving ${T::class}" } }
                .map { frame ->
                    either {
                        val frameText = (frame as Frame.Text).readText()
                            .also { logger.debug { "Incoming payload: $it" } }

                        recover<CommonError, T>(
                            block = {
                                with(serialization) { frameText.deserialize<T>().bind() }
                            },
                            recover = {
                                if (it is Serialization.Error) {
                                    val incomingError = recover({
                                        with(serialization) { frameText.deserialize<CommonError>().bind() }
                                    }) {
                                        raise(Connection.Error("Unhandled incoming message", it))
                                    }

                                    raise(incomingError)
                                } else raise(Connection.Error("Error on receive", it))
                            }
                        ).also { logger.debug { "Received $it" } }
                    }.onLeft {
                        with(terminalErrorHandler) { it.handle() }
                    }
                }
        }

}