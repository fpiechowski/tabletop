package tabletop.server

import arrow.core.raise.Raise
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
import tabletop.common.event.RequestEvent
import tabletop.common.server.Server
import tabletop.server.di.ConnectionDependencies
import tabletop.server.di.Dependencies
import java.io.File

class ServerAdapter(
    private val dependencies: Dependencies,
) : Server() {
    private val logger = KotlinLogging.logger { }
    private val application: CompletableDeferred<Application> = CompletableDeferred()

    fun launch() = either {
        recover<CommonError, Unit>({
            embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = {
                eventsModule()
            }).start(wait = true)
        }, recover = {
            raise(Error("Error on starting server", it))
        }, catch = {
            raise(Error("Error on starting server", CommonError.ThrowableError(it)))
        })
    }

    private fun Application.eventsModule() {
        application.complete(this)
        install(WebSockets)
        launch {
            serveEvents()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun serveEvents() {
        application.await().routing {
            get("/status") {
                call.respond(HttpStatusCode.OK, "healthy")
            }

            staticFiles("/assets", File("assets").also { logger.info { "Serving assets from absolute path: ${it.absolutePath}" } })

            webSocket {
                val connection = with(this.call.request.origin) {
                    Connection(remoteHost, remotePort, this@webSocket)
                }
                val connectionScopeDependencies = dependencies.connectionDependenciesFactory(connection)

                with(connectionScopeDependencies) {
                    recover<CommonError, Unit>(
                        block = {
                            dependencies.state.connections.update { it + connection }

                            receiveIncomingEvents(connectionScopeDependencies) {
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

    private suspend fun receiveIncomingEvents(
        connectionScopeDependencies: ConnectionDependencies,
        onEach: suspend Raise<CommonError>.(Event) -> Unit
    ) = with(connectionScopeDependencies) {
        logger.debug { "Receiving incoming events" }
        connectionCommunicator.receiveIncoming<RequestEvent>({
            onEach(it)
        }) {
            with(connectionErrorHandler) { it.handle() }
        }
    }

    companion object;

}
