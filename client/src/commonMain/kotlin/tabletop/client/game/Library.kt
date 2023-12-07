package tabletop.client.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.uuid.UUID
import tabletop.client.di.Dependencies
import tabletop.client.ui.WindowModel
import tabletop.common.entity.Entity
import tabletop.common.event.SceneOpeningRequested
import tabletop.common.scene.Scene

@ExperimentalComposeUiApi
class Library(
    val dependencies: Dependencies
) {
    val windowPosition: MutableStateFlow<IntOffset> = MutableStateFlow(IntOffset.Zero)

    @Composable
    fun LibraryButton( modifier: Modifier) {
        val windowsOpened = dependencies.userInterface.openedWindows

        Button(onClick = {
            if (windowsOpened.value.containsKey(UUID(GameScreen.libraryWindowModelId))) {
                windowsOpened.value -= UUID(GameScreen.libraryWindowModelId)
            } else {
                windowsOpened.value += LibraryWindowModel(windowPosition) {
                    LibraryWindowContent()
                }.let { it.id to it }
            }
        }, modifier = modifier) {
            Icon(Icons.Default.LibraryBooks, contentDescription = "Library")
        }
    }

    @Composable
    fun LibraryWindowContent() {
        val maybeGame by dependencies.state.maybeGame.collectAsState()
        val searchText = remember { mutableStateOf("") }

        maybeGame?.let { game ->
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(searchText.value, modifier = Modifier.fillMaxWidth(), onValueChange = {
                    searchText.value = it
                })
                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
                    this.items(game.entities.values.filter {
                        it.name.lowercase().indexOf(searchText.value.lowercase()) != -1
                    }.toList()) {
                        LibraryEntityGridItem(it)
                    }
                }
            }
        }
    }

    @Composable
    fun LibraryEntityGridItem(entity: Entity) {
        Box(
            modifier = Modifier
                .clickable {
                    when (entity) {
                        is Scene -> with(dependencies.eventHandler) {
                            launch { SceneOpeningRequested(entity.id).handle() }
                        }
                    }
                }
        ) {
            entity.image?.let {

                //TODO
            }
            Text(entity.name, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    private fun LibraryWindowModel(offsetState: MutableStateFlow<IntOffset>, content: @Composable () -> Unit) =
        WindowModel(
            "Library",
            Modifier.width(600.dp),
            offsetState,
            UUID(GameScreen.libraryWindowModelId),
            content
        )
}