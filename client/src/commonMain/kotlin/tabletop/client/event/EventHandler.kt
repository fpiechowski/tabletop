package tabletop.client.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.client.di.Dependencies
import tabletop.client.game.GameScene
import tabletop.client.server.ServerAdapter
import tabletop.common.command.Command
import tabletop.common.error.CommonError
import korlibs.korge.scene.Scene as UIScene

class EventHandler(
    private val dependencies: Dependencies
) {
    private val logger = KotlinLogging.logger {}

    private val userInterface by lazy { dependencies.userInterface }

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    suspend fun <T : Event> T.handle(): Either<Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover<CommonError, Unit>({
                when (this@handle) {
                    is ConnectionAttempted ->
                        ServerAdapter(dependencies).connect(host, port, credentialsData).bind()

                    is UserAuthenticated -> {
                        handle(userInterface.connectionScene)

                        dependencies.connectionScope()?.run {
                            with(connectionCommunicator) {
                                (Command.GetGames(user.id) as Command)
                                    .send()
                                    .bind()
                            }
                        }
                    }

                    is LoadingGameAttempted -> {
                        dependencies.connectionScope()?.run {
                            with(connectionCommunicator) {
                                (Command.GetGame(gameListingItem.id) as Command)
                                    .send()
                                    .bind()
                            }
                        }
                    }

                    is GameLoaded -> {
                        userInterface.sceneContainer.await().changeTo { GameScene(game) }
                    }

                    else -> {}
                }
            }) {
                raise(Error("Error when handling $this", it))
            }
        }

    fun <T : UIEvent<*>> T.handle(uiScene: UIScene): Either<Error, Unit> =
        either {
            logger.debug { "Handling ${this@handle}" }

            recover<CommonError, Unit>({
                uiScene.sceneView.dispatch(this@handle)
            }) {
                raise(Error("Error when handling $this", it))
            }
        }
}