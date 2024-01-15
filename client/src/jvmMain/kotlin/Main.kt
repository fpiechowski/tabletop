import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.di.Dependencies
import tabletop.client.dnd5e.character.CharacterView
import tabletop.shared.ApplicationInfo
import tabletop.shared.demo.demoCharacter


@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
fun main() =
    application {
        MaterialTheme {
            Window(onCloseRequest = ::exitApplication, title = ApplicationInfo.Name) {
                tabletop.client.application()
            }
        }
    }

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
@ExperimentalLayoutApi
@Preview
fun preview() {
    tabletop.client.ui.Window(
        "Character Sheet",
        MutableStateFlow(mapOf())
    ) {
        CharacterView(demoCharacter, Dependencies())
    }
}