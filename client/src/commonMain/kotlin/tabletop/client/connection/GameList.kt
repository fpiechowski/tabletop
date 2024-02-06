package tabletop.client.connection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.shared.error.ErrorHandler.Companion.use
import tabletop.shared.event.GameLoadingRequested
import tabletop.shared.game.Game

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class GameList(
    val dependencies: Dependencies,
) {

    private val games: MutableValue<Set<Game<*>>> = MutableValue(setOf())

    @Composable
    fun content() =
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            games.value.forEach {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(it.name, style = MaterialTheme.typography.titleMedium)
                        Button(onClick = {
                            dependencies.run {
                                with(eventHandler) {
                                    launch {
                                        errorDialogs.use {
                                            GameLoadingRequested(it.id).handle().bind()
                                        }
                                    }
                                }
                            }
                        }) {
                            Text("Load Game")
                        }
                    }
                }
            }
        }
}