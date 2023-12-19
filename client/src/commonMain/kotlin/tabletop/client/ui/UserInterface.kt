package tabletop.client.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.ExperimentalComposeUiApi
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


@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class UserInterface(private val dependencies: Dependencies) : CoroutineScope {


    val snackbarHostState = SnackbarHostState()
    val openedWindows: MutableStateFlow<Map<UUID, WindowModel>> = MutableStateFlow(mapOf())
    val connectionScreenModel = CompletableDeferred<ConnectionScreen.Model>()

    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Default
}

