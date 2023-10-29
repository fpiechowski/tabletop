package tabletop.client.server

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import tabletop.client.di.DependenciesAdapter
import tabletop.common.auth.Credentials
import tabletop.common.command.Command
import tabletop.common.command.CommandResult
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.handleErrors
import tabletop.common.server.Server

class ServerAdapter(
    private val dependencies: DependenciesAdapter
) : Server() {
    private val logger = KotlinLogging.logger { }
    private val commandResultExecutor by lazy { dependencies.commandResultExecutor }
    private val eventHandler by lazy { dependencies.eventHandler }
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }

    suspend fun connect(
        host: String,
        port: Int,
        credentials: Credentials.UsernamePassword.Data
    ): Either<Error, Unit> = either {
        logger.info { "Connecting to server $host:$port with principal = ${credentials.username}" }
        catch({
            httpClient
                .webSocket(host = host, port = port) {
                    val connection = Connection(this, TMVar.empty())
                    val connectionScopeDependencies = dependencies.ConnectionScope(connection)

                    recover<CommonError, Unit>({
                        with(connectionScopeDependencies.connectionCommunicator) {
                            (Command.SignIn(credentials.username, credentials.password) as Command).send()
                        }

                        receiveIncomingCommandResults(connectionScopeDependencies) {
                            with(commandResultExecutor) {
                                it.execute().bind()
                            }
                        }
                    }) {
                        close(CloseReason(CloseReason.Codes.GOING_AWAY, "Connection ended"))
                        raise(Error("Connection with server ended with error", it))
                    }
                }
        }) { raise(Error("Error on connecting to TabletopServer", CommonError.ThrowableError(it))) }
    }

    private suspend fun receiveIncomingCommandResults(
        connectionScopeDependencies: DependenciesAdapter.ConnectionScope,
        onEach: suspend Raise<CommonError>.(Command.Result<Command, Command.Result.Data>) -> Unit
    ) = with(uiErrorHandler) {
        connectionScopeDependencies.connectionCommunicator.receiveIncomingAsEithersFlow<CommandResult>()
            .handleErrors(uiErrorHandler)
            .transform { result ->
                recover({ onEach(result) }) { it.handle() }
                emit(result)
            }.collect()
    }


    companion object {
        val httpClient: HttpClient = HttpClient {
            install(WebSockets)
        }
    }
}

