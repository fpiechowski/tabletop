package tabletop.client.ui

import korlibs.korge.scene.SceneContainer
import korlibs.korge.view.Stage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.Serializable
import tabletop.client.connection.ConnectionScene
import tabletop.client.game.GameScene
import tabletop.common.error.CommonError


class UserInterface {

    val stage = CompletableDeferred<Stage>()
    val sceneContainer = CompletableDeferred<SceneContainer>()
    val connectionScene = ConnectionScene()
    val gameScene = GameScene()

    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

