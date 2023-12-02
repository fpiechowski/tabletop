import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.navigation.Navigation
import tabletop.common.error.CommonError


fun main() = Dependencies().run {
    application {
        MaterialTheme {
            Window(onCloseRequest = ::exitApplication) {
                Navigator(ConnectionScreen(this@run)) {
                    navigation.complete(Navigation(userInterface, it))

                    Scaffold {
                        CurrentScreen()
                    }
                }
            }
        }
    }
}
