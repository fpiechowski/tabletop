package tabletop.client.ui

import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.game.GameScene
import tabletop.common.error.CommonError


class UserInterface(private val dependencies: Dependencies) {
    val gameScene: Store<GameScene> = storeOf(GameScene(dependencies), Job())
    val connectionScreen: Store<ConnectionScreen> = storeOf(ConnectionScreen(dependencies), Job())
    val notifications: Notifications = Notifications(dependencies)

    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

