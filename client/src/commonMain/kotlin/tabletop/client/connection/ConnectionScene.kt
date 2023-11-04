package tabletop.client.connection

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.event.EventType
import korlibs.korge.input.onClick
import korlibs.korge.scene.Scene
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiText
import korlibs.korge.ui.uiTextInput
import korlibs.korge.ui.uiWindow
import korlibs.korge.view.SContainer
import korlibs.korge.view.View
import korlibs.korge.view.align.*
import korlibs.korge.view.container
import korlibs.korge.view.xy
import korlibs.math.geom.Size
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.ConnectionAttempted
import tabletop.client.event.LoadingGameAttempted
import tabletop.client.event.UIEvent
import tabletop.client.event.UserAuthenticated
import tabletop.common.auth.Credentials
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.game.Game

class ConnectionScene : Scene() {
    private val logger = KotlinLogging.logger { }


    override suspend fun SContainer.sceneMain() {
        gameListing()
        connectionWindow()
    }

    class GameListingUpdated(
        val gameListing: Game.Listing
    ) : UIEvent<GameListingUpdated>(GameListingUpdated) {

        companion object : EventType<GameListingUpdated>
    }

    private fun SContainer.gameListing() =
        onEvent(GameListingUpdated) { event ->
            container {
                this@container.removeChildren()

                event.gameListing.games.map { gameListingItem ->
                    uiButton(gameListingItem.name) {
                        onClick {
                            with(Dependencies.await().eventHandler) {
                                LoadingGameAttempted(gameListingItem).handle()
                            }
                        }
                    }
                }.fold(listOf<View>()) { prevList, next ->
                    prevList.lastOrNull()
                        ?.let { next.alignLeftToRightOf(it, 50) }
                    prevList + next
                }.also { logger.debug { "Updated gameListingView with $it" } }
            }.centerOnStage()
        }

    private fun SContainer.connectionWindow() =
        uiWindow("Connection", size = Size(488, 172)) { window ->
            onEvent(UserAuthenticated) {
                launch { window.closeAnimated() }
            }

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
                    with(Dependencies.await().eventHandler) {
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
        }.centerOnStage()


    private fun Raise<Connection.Error>.parseServerUrl(serverUrl: String) = catch({
        serverUrl.split(":", limit = 2).let { it[0] to it[1].toInt() }
    }) { raise(Connection.Error("Can't parse server URL", CommonError.ThrowableError(it))) }
}