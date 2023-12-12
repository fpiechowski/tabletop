package tabletop.client.server

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import tabletop.client.di.ConnectionDependencies
import tabletop.client.di.Dependencies
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.common.auth.Credentials
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.event.AuthenticationRequested
import tabletop.common.event.ResultEvent
import tabletop.common.server.Server

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class ServerAdapter(
    private val dependencies: Dependencies,
    private val eventHandler: EventHandler,
    private val uiErrorHandler: UIErrorHandler,
) : Server() {
    private val logger = KotlinLogging.logger { }

    suspend fun connect(
        host: String,
        port: Int,
        credentials: Credentials.UsernamePassword.Data
    ): Either<Error, Unit> = either {
        logger.info { "Connecting to server $host:$port with principal = ${credentials.username}" }
        catch({
            httpClient
                .webSocket(host = host, port = port) {
                    val connection = Connection(host, port, this)
                    val connectionDependencies = dependencies.connectionDependenciesFactory(connection)

                    recover<CommonError, Unit>({
                        with(eventHandler) {
                            AuthenticationRequested(credentials).handle().bind()

                            receiveIncomingResultEvents(connectionDependencies) {
                                it.handle().bind()
                            }

                            logger.warn { "Completed receiving command results" }
                        }
                    }, catch = {
                        close(CloseReason(CloseReason.Codes.GOING_AWAY, "Connection ended"))
                        raise(Error("Connection with server ended with error", CommonError.ThrowableError(it)))
                    }, recover = {
                        close(CloseReason(CloseReason.Codes.GOING_AWAY, "Connection ended"))
                        raise(Error("Connection with server ended with error", it))
                    })

                    logger.debug { "$connection ending" }
                }
        }) { raise(Error("Error on connecting to TabletopServer", CommonError.ThrowableError(it))) }

        logger.warn { "Connection ended" }
    }

    private suspend fun receiveIncomingResultEvents(
        connectionScopeDependencies: ConnectionDependencies,
        onEach: suspend (ResultEvent) -> Unit
    ) = with(connectionScopeDependencies) {
        connectionCommunicator.receiveIncoming<ResultEvent>({
            onEach(it)
        }) {
            with(uiErrorHandler) { it.handle() }
        }
    }


    companion object {
        val httpClient: HttpClient = HttpClient {
            install(WebSockets)
        }
    }
}

