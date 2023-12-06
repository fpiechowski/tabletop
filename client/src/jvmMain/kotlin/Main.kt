import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.navigation.Navigation
import tabletop.common.ApplicationInfo


@ExperimentalComposeUiApi
fun main() = Dependencies().run {
    application {
        MaterialTheme {
            Window(onCloseRequest = ::exitApplication, title = ApplicationInfo.Name) {

                Navigator(ConnectionScreen(this@run)) {
                    navigation.complete(Navigation(userInterface, it))

                    Scaffold{
                        CurrentScreen()
                    }
                }
            }
        }
    }
}
