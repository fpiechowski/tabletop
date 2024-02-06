package tabletop.client.connection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.client.di.Dependencies

private val logger = KotlinLogging.logger { }


@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class ConnectionScreen(
    val dependencies: Dependencies,
) {

    private val gameList = GameList(dependencies.childDependencies("gameList"))
    private val connectionWindow = ConnectionWindow(dependencies.childDependencies("connectionWindow"))

    @ExperimentalComposeUiApi
    @ExperimentalMaterial3Api
    @ExperimentalLayoutApi
    @Composable
    fun content() {
        val gamesState = dependencies.state.games
        val games by gamesState.collectAsState()

        dependencies.errorDialogs.errorDialog()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (games.isEmpty()) {
                    connectionWindow.content()
                } else {
                    gameList.content()
                }
            }
        }
    }





}

