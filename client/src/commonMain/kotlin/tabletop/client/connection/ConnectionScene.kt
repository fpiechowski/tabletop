package tabletop.client.connection

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import dev.fritz2.core.RenderContext
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.ConnectionAttempted
import tabletop.client.state.State
import tabletop.client.ui.draggableWindow
import tabletop.common.auth.Credentials
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler.Companion.use
import tabletop.common.event.GameLoadingRequested
import tabletop.client.event.UserAuthenticatedUIEvent.Companion as UserAuthenticated


class ConnectionScene(
    val state: State
) {
    suspend fun RenderContext.render() {
        gameListing()
        connectionWindow()
    }

    private fun RenderContext.gameListing() =
        div {
            state.gameListing.data.render {
                it?.games?.map { gameListingItem ->
                    button {
                        +gameListingItem.name
                        clicks handledBy {
                            Dependencies.instance.await().run {
                                with(eventHandler) {
                                    uiErrorHandler.use {
                                        GameLoadingRequested(gameListingItem).handle().bind()
                                    }
                                }
                            }
                        }
                    }
                }.also { console.log("Updated gameListingView with $it") }
            }
        }
}

private fun Raise<Connection.Error>.parseServerUrl(serverUrl: String) = catch({
    serverUrl.split(":", limit = 2).let { it[0] to it[1].toInt() }
}) { raise(Connection.Error("Can't parse server URL", CommonError.ThrowableError(it))) }


fun RenderContext.connectionWindow() =
    draggableWindow("Connection") {
        state.authenticatedUser.data.onEach {

        }
        onEvent(UserAuthenticated) {
            scene.launch { closeAnimated() }
        }

        with(this.container) {
            val serverUrl = uiText("Host:").xy(20, 20)
            val serverUrlInput = uiTextInput(initialText = "localhost:8080", size = Size(200, 24))
                .alignLeftToRightOf(serverUrl)
                .centerYOn(serverUrl)

            val usernameText = uiText("Username:")
                .alignTopToBottomOf(serverUrl, 20)
                .alignLeftToLeftOf(serverUrl)
            val usernameInput = uiTextInput(size = Size(200, 24))
                .alignLeftToRightOf(usernameText)
                .centerYOn(usernameText)

            val passwordText = uiText("Password:")
                .alignTopToBottomOf(usernameText, 20)
                .alignLeftToLeftOf(usernameText)
            val passwordInput = uiTextInput(size = Size(200, 24))
                .alignLeftToRightOf(passwordText)
                .centerYOn(passwordText)

            connectionButton = uiButton("Connect", size = Size(100f, 112f))
                .alignLeftToRightOf(serverUrlInput, 20)
                .alignTopToTopOf(serverUrlInput)
                .centerYOn(usernameInput)
                .also {
                    it.onClick {
                        logger.debug { "connectionButton.onClick" }
                        Dependencies.instance.await().run {
                            with(eventHandler) {
                                either {
                                    val (host, port) = parseServerUrl(serverUrlInput.text)

                                    scene.launch {
                                        uiErrorHandler.use {
                                            ConnectionAttempted(
                                                host = host,
                                                port = port,
                                                credentialsData = Credentials.UsernamePassword.Data(
                                                    usernameInput.text,
                                                    passwordInput.text
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

