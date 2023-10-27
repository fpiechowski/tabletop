package tabletop.client.server

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
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
    private val commandChannel by lazy { dependencies.commandChannel }
    private val commandResultChannel by lazy { dependencies.commandResultChannel }
    private val commandResultExecutor by lazy { dependencies.commandResultExecutor }
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }
    private val terminalErrorHandler by lazy { dependencies.terminalErrorHandler }

    suspend fun connect(
        host: String,
        port: Int,
        credentials: Credentials.UsernamePassword.Data
    ): Either<Error, Unit> = either {
        catch({
            httpClient
                .webSocket(host = host, port = port) {
                    val connection = Connection(this, TMVar.empty())
                    val connectionScopeDependencies = dependencies.ConnectionScope(connection)

                    with(connectionScopeDependencies) {
                        recover<CommonError, Unit>({
                            launchCommandResultProcessing(connectionScopeDependencies)
                            launchCommandProcessing(connectionScopeDependencies)

                            with(commandChannel) {
                                Command.SignIn(credentials.username, credentials.password).publish()
                            }

                            receiveIncomingCommandResults(connectionScopeDependencies) {
                                with(commandResultChannel) { it.publish() }
                            }
                        }) {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Bye bye"))
                            raise(Error("", it))
                        }
                    }
                }
        }) { raise(Error("Error on connecting to TabletopServer", CommonError.ThrowableError(it))) }
    }

    private fun CoroutineScope.launchCommandProcessing(connectionScopeDependencies: DependenciesAdapter.ConnectionScope) =
        launch {
            commandChannel
                .receiveAsFlow {
                    recover<CommonError, Unit>({
                        with(connectionScopeDependencies.connectionCommunicator) {
                            it.send().bind()
                        }
                    }) {
                        with(uiErrorHandler) {
                            Error("Error on sending $it to server").handle()
                        }
                    }
                }
        }

    @Suppress("UNCHECKED_CAST")
    private fun CoroutineScope.launchCommandResultProcessing(connectionScopeDependencies: DependenciesAdapter.ConnectionScope) =
        launch {
            commandResultChannel
                .receiveAsFlow {
                    recover({
                        with(commandResultExecutor) {
                            (it as CommandResult).execute().bind()
                        }
                    }) {
                        with(uiErrorHandler) {
                            Error("Error on executing $it").handle()
                        }
                    }
                }
        }


    private suspend fun receiveIncomingCommandResults(
        connectionScopeDependencies: DependenciesAdapter.ConnectionScope,
        onEach: suspend Raise<CommonError>.(Command.Result<Command, Command.Result.Data>) -> Unit
    ) = with(uiErrorHandler) {
        connectionScopeDependencies.connectionCommunicator.receiveIncomingAsEithersFlow<CommandResult>()
            .handleErrors(terminalErrorHandler)
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

