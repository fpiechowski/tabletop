package tabletop.client.ui

import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.game.GameScreen
import tabletop.common.error.CommonError
import kotlin.coroutines.CoroutineContext


class UserInterface(private val dependencies: Dependencies) : CoroutineScope {
    val gameScreen: Store<GameScreen> = storeOf(GameScreen(dependencies), Job())
    val connectionScreen: Store<ConnectionScreen> = storeOf(ConnectionScreen(dependencies), Job())
    val notifications: Notifications = Notifications(dependencies)

    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
}

