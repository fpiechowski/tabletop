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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.seiko.imageloader.rememberImagePainter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.uuid.UUID
import tabletop.client.di.Dependencies
import tabletop.client.ui.Window
import tabletop.client.ui.WindowModel
import tabletop.common.entity.Entity
import tabletop.common.game.Game
import tabletop.common.scene.Scene

class GameScreen(
    private val dependencies: Dependencies
) : Screen {
    data class Model(
        val game: MutableStateFlow<Game<*>?>,
        val windowsOpened: MutableStateFlow<Map<UUID, WindowModel>>,
        val libraryWindowPosition: MutableStateFlow<IntOffset> = MutableStateFlow(IntOffset(100, 100))
    ) : ScreenModel

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel {
            Model(dependencies.state.maybeGame, dependencies.userInterface.openedWindows)
                .also { dependencies.userInterface.gameScreenModel.complete(it) }
        }

        val windowsOpened by screenModel.windowsOpened.collectAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            SceneView(screenModel)

            LibraryButton(screenModel, Modifier.align(Alignment.TopStart).padding(16.dp))

            windowsOpened.forEach { (_, it) ->
                Window(it.title, it.modifier, dependencies.userInterface, it.offsetState, it.id) {
                    it.content()
                }
            }
        }
    }

    @Composable
    fun LibraryButton(screenModel: Model, modifier: Modifier) {
        Button(onClick = {
            if (screenModel.windowsOpened.value.containsKey(UUID(libraryWindowModelId))) {
                screenModel.windowsOpened.value -= UUID(libraryWindowModelId)
            } else {
                screenModel.windowsOpened.value += LibraryWindowModel(screenModel.libraryWindowPosition) {
                    LibraryWindowContent(screenModel)
                }.let { it.id to it }
            }
        }, modifier = modifier) {
            Icon(Icons.Default.LibraryBooks, contentDescription = "Library")
        }
    }

    @Composable
    fun LibraryWindowContent(screenModel: Model) {
        val maybeGame by screenModel.game.collectAsState()
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
                        is Scene -> dependencies.state.currentScene.value = entity
                    }
                }
        ) {
            entity.image?.let {

                Image(
                    painter = rememberImagePainter(
                        dependencies.state.connectionDependencies.value!!.serverUrl(it).toString()
                    ),
                    contentDescription = entity.name,
                    modifier = Modifier.size(100.dp, 100.dp)
                )
            }
            Text(entity.name, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    private fun LibraryWindowModel(offsetState: MutableStateFlow<IntOffset>, content: @Composable () -> Unit) =
        WindowModel(
            "Library",
            Modifier.width(600.dp),
            offsetState,
            UUID(libraryWindowModelId),
            content
        )

    @Composable
    fun SceneView(screenModel: Model) {
        dependencies.state.currentScene.value
            ?.let {
                it.foregroundImagePath?.let {
                    val painter = rememberImagePainter(dependencies.state.connectionDependencies.value?.serverUrl(it).toString())
                    //Image(painter)
                }
            }
    }

    companion object {
        const val libraryWindowModelId = "5d4d1968-7fbd-4d67-9320-650c65671815"
    }
}