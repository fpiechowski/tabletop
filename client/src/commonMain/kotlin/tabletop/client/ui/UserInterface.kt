package tabletop.client.ui

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.uuid.UUID
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.game.GameScreen
import tabletop.common.error.CommonError
import kotlin.coroutines.CoroutineContext


class UserInterface(private val dependencies: Dependencies) : CoroutineScope {

    val openedWindows: MutableStateFlow<Map<UUID, WindowModel>> = MutableStateFlow(mapOf())
    val gameScreenModel = CompletableDeferred<GameScreen.Model>()
    val connectionScreenModel = CompletableDeferred<ConnectionScreen.Model>()

    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
}

