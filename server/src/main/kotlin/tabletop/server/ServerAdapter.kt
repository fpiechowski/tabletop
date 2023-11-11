package tabletop.server

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.event.Event
import tabletop.common.server.Server
import tabletop.server.di.Dependencies
import java.io.File

class ServerAdapter(
    private val connectionScopeFactory: Dependencies.ConnectionScopeFactory
) : Server() {
    private val logger = KotlinLogging.logger { }
    val application: CompletableDeferred<Application> = CompletableDeferred()

    fun launch() = either {
        catch({
            embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
                module().also {
                    application.complete(this)
                }
            }.start(wait = true)
        }) {
            raise(Error("Error on starting server", CommonError.ThrowableError(it)))
        }
    }

    private fun Application.module() = apply {
        launch {
            install(WebSockets)
            serve()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun serve() {
        application.await().routing {
            staticFiles("/assets", File("assets"))

            get("/status") {
                call.respond(HttpStatusCode.OK, "healthy")
            }

            webSocket {


                val connection = with(this.call.request.origin) {
                    Connection(remoteHost, remotePort, this@webSocket)
                }
                val connectionScopeDependencies = connectionScopeFactory(connection)

                with(connectionScopeDependencies) {
                    recover<CommonError, Unit>(
                        block = {
                            state.connections.update { it + connection }

                            receiveIncomingCommands(connectionScopeDependencies) {
                                with(eventHandler) {
                                    it.handle().bind()
                                }
                            }
                        },
                        catch = {
                            with(connectionErrorHandler) { CommonError.ThrowableError(it).handle() }
                            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, it.message ?: "unknown error"))
                        },
                        recover = {
                            with(connectionErrorHandler) { it.handle() }
                            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, it.message ?: "unknown error"))
                        }
                    )
                }
            }
        }
    }

    private suspend fun receiveIncomingCommands(
        connectionScopeDependencies: Dependencies.ConnectionScope,
        onEach: suspend Raise<CommonError>.(Event) -> Unit
    ) = with(connectionScopeDependencies) {
        connectionCommunicator.receiveIncoming<Event>({
            onEach(it)
        }) {
            with(connectionErrorHandler) { it.handle() }
        }
    }

    companion object;

}
