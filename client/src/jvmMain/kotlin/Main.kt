import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import tabletop.shared.ApplicationInfo


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

