package tabletop.client

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.navigator.CurrentScreen
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.game.GameScreen
import tabletop.client.navigation.Navigation

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun application(dependencies: Dependencies) {

    Scaffold {
        CurrentScreen()
    }

    with(dependencies) {
        errorDialogs.errorDialog()

        when (navigation.currentScreen.value) {
            Navigation.Screen.Connection -> ConnectionScreen(
                dependencies.childDependencies("connectionScreen")
            ).content()

            Navigation.Screen.Game -> GameScreen(
                dependencies.childDependencies("gameScreen")
            ).content()
        }
    }
}