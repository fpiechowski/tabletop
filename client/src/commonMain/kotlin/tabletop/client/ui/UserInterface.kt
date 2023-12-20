package tabletop.client.ui

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.uuid.UUID
import tabletop.client.di.Dependencies
import tabletop.shared.error.CommonError


@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class UserInterface(private val dependencies: Dependencies) {

    val openedWindows: MutableStateFlow<Map<UUID, WindowModel>> = MutableStateFlow(mapOf())

    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

