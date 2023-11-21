package tabletop.client.connection

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.onEach
import tabletop.client.di.Dependencies
import tabletop.client.event.ConnectionAttempted
import tabletop.client.ui.textField
import tabletop.client.ui.window
import tabletop.common.auth.Credentials
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler.Companion.use
import tabletop.common.event.GameLoadingRequested
import tabletop.client.ui.button as fancyButton

fun RenderContext.connectionScene(dependencies: Dependencies) = with(ConnectionScreen(dependencies)) { render() }

class ConnectionScreen(
    val dependencies: Dependencies
) {
    private val logger = KotlinLogging.logger {}

    private val hostStore = storeOf("localhost:8080", Job())
    val usernameStore = storeOf("gm", Job())
    val passwordStore = storeOf("gm", Job())

    fun RenderContext.render() {
        div("flex items-center justify-center h-screen w-full", id = "connectionScene") {
            gameListing()
            dependencies.state.user.data.render {
                if (it == null) {
                    connectionWindow()
                }
            }
        }
    }

    private fun RenderContext.gameListing() =
        div("flex flex-row gap-5",id = "gameListing") {
            dependencies.state.games.data.render {
                it.map { game ->
                    fancyButton(game.name) {
                        clicks handledBy {
                            Dependencies.instance.await().run {
                                with(eventHandler) {
                                    uiErrorHandler.use {
                                        GameLoadingRequested(game.id).handle().bind()
                                    }
                                }
                            }
                        }
                    }
                }.also { logger.debug { "Updated gameListingView with $it" } }
            }
        }

    private fun RenderContext.connectionWindow() =
        div("max-w-sm w-96") {
            window("Connection") {
                dependencies.state.user.data.onEach {
                    TODO("close connection window")
                }

                div("flex flex-col place-content-between gap-2") {
                    textField("Host", hostStore)
                    textField("Username", usernameStore)
                    textField("Password", passwordStore, "password")
                    fancyButton("Connect", "self-center") {
                        clicks handledBy {
                            logger.debug { "connectionButton.onClick" }
                            Dependencies.instance.await().run {
                                logger.debug { eventHandler.toString() }
                                with(eventHandler) {
                                    either<CommonError, Any> {
                                        uiErrorHandler.use {
                                            val (host, port) = parseServerUrl(hostStore.current)

                                            ConnectionAttempted(
                                                host = host,
                                                port = port,
                                                credentialsData = Credentials.UsernamePassword.Data(
                                                    usernameStore.current,
                                                    passwordStore.current
                                                )
                                            ).handle().bind()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun Raise<Connection.Error>.parseServerUrl(serverUrl: String) = catch({
        serverUrl.split(":", limit = 2).let { it[0] to it[1].toInt() }
    }) { raise(Connection.Error("Can't parse server URL", CommonError.ThrowableError(it))) }
}





