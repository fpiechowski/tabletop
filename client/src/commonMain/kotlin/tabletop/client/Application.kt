package tabletop.client

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import tabletop.client.connection.ConnectionScreen
import tabletop.client.di.Dependencies
import tabletop.client.navigation.Navigation

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun application() {
    Dependencies().run {
        Navigator(ConnectionScreen(this@run)) {
            navigation.complete(Navigation(it))

            Scaffold {
                CurrentScreen()
            }

            uiErrorHandler.errorDialog()
        }
    }
}