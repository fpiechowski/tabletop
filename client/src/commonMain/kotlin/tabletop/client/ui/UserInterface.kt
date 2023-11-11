package tabletop.client.ui

import tabletop.client.connection.ConnectionScene
import tabletop.client.game.GameScene
import tabletop.client.state.State
import tabletop.common.error.CommonError


class UserInterface(val state: State) {

    val connectionScene = ConnectionScene(state)
    val gameScene = GameScene()

    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

