package tabletop.client.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.arkivanov.decompose.value.MutableValue
import tabletop.client.di.Dependencies

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class Navigation(
    private val dependencies: Dependencies,
) {
    val currentScreen: MutableValue<Screen> = MutableValue(Screen.Connection)

    enum class Screen {
        Connection, Game
    }
}