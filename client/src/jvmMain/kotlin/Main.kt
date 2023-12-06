import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.navigation.Navigation
import tabletop.common.ApplicationInfo
import tabletop.common.error.CommonError


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
