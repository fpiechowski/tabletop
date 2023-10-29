package tabletop.client.connection

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import korlibs.korge.input.onClick
import korlibs.korge.scene.Scene
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiText
import korlibs.korge.ui.uiTextInput
import korlibs.korge.ui.uiWindow
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.*
import korlibs.korge.view.xy
import korlibs.math.geom.Size
import kotlinx.coroutines.launch
import tabletop.client.di.DependenciesAdapter
import tabletop.client.event.ConnectionAttempted
import tabletop.common.auth.Credentials
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError

class ConnectionScene : Scene() {

    override suspend fun SContainer.sceneMain() {
        val eventHandler = injector.get<DependenciesAdapter>().eventHandler

        uiWindow("Connection", size = Size(488, 172)) {
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

            uiButton("Connect", size = Size(100f, 112f))
                .alignLeftToRightOf(serverUrlInput, 20)
                .alignTopToTopOf(serverUrlInput)
                .centerYOn(usernameInput)
                .onClick {
                    with(eventHandler) {
                        either {
                            val (host, port) = parseServerUrl(serverUrlInput.text)

                            launch {
                                ConnectionAttempted(
                                    host = host,
                                    port = port,
                                    credentialsData = Credentials.UsernamePassword.Data(
                                        usernameInput.text,
                                        passwordInput.text
                                    )
                                ).handle()
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