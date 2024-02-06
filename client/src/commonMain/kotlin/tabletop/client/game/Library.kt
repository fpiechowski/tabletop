package tabletop.client.game

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import arrow.core.getOrElse
import arrow.core.raise.either
import arrow.core.raise.recover
import com.arkivanov.decompose.value.MutableValue
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.uuid.UUID
import tabletop.client.di.Dependencies
import tabletop.client.dnd5e.character.CharacterSheet
import tabletop.client.io.loadImageFile
import tabletop.client.ui.AsyncImage
import tabletop.client.ui.TokenizableDragging
import tabletop.client.ui.Window
import tabletop.shared.dnd5e.character.Character
import tabletop.shared.entity.Entity
import tabletop.shared.event.SceneOpeningRequested
import tabletop.shared.event.TokenPlacingRequested
import tabletop.shared.geometry.Point
import tabletop.shared.plus
import tabletop.shared.scene.Scene
import tabletop.shared.scene.token.Tokenizable
import kotlin.math.roundToInt

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class Library(
    val dependencies: Dependencies
) {
    private val logger = KotlinLogging.logger { }
    val windowPosition: MutableValue<IntOffset> = MutableValue(IntOffset.Zero)

    @Composable
    fun button(modifier: Modifier) {
        val windowsOpened = dependencies.windows.openedWindows

        Button(onClick = {
            if (windowsOpened.value.containsKey(UUID(GameScreen.libraryWindowModelId))) {
                windowsOpened.value -= UUID(GameScreen.libraryWindowModelId)
            } else {
                windowsOpened.value += window {
                    content()
                }
            }
        }, modifier = modifier) {
            Icon(Icons.Default.LibraryBooks, contentDescription = "Library")
        }
    }

    private fun window(content: @Composable () -> Unit) =
        Window(
            dependencies.childDependencies("libraryWindow"),
            "Library",
            Modifier.width(600.dp),
            windowPosition,
            UUID(GameScreen.libraryWindowModelId),
            content
        )

    @Composable
    fun content() {
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
                        entityGridItem(it)
                    }
                }
            }
        }
    }


    //TODO could clean up all these child composables
    @Composable
    fun entityGridItem(entity: Entity) {
        val positionInRoot = remember { mutableStateOf(Offset.Zero) }
        val coroutineScope = rememberCoroutineScope()

        Box(
            modifier = Modifier
                .onGloballyPositioned { positionInRoot.value = it.positionInRoot() }
                .clickable { clickableEntityHandler(entity) }
                .pointerInput(Unit) { tokenizableDraggingPointerInputHandler(entity, coroutineScope, positionInRoot) }
        ) {
            entity.image?.let {
                either {
                    AsyncImage(
                        load = {
                            loadImageFile(
                                with(dependencies.state) {
                                    connectionDependencies.ensureNotNull().bind().assets.assetFile(it).bind()
                                }
                            )
                        },
                        painterFor = { remember { BitmapPainter(it) } },
                        contentDescription = entity.name,
                        modifier = Modifier.size(100.dp)
                    )
                }.getOrElse {
                    with(dependencies.terminalErrorHandler) { coroutineScope.launch { it.handle() } }
                    Icon(Icons.Default.Error, it.message)
                }
            }
            Text(entity.name, modifier = Modifier.align(Alignment.BottomCenter))
        }
    }

    private fun clickableEntityHandler(entity: Entity) {
        when (entity) {
            is Scene -> with(dependencies.eventHandler) {
                launch { SceneOpeningRequested(entity.id).handle() }
            }

            is Character -> {
                dependencies.windows.openedWindows.value += CharacterSheet(dependencies, entity).window()
            }
        }
    }

    private suspend fun PointerInputScope.tokenizableDraggingPointerInputHandler(
        entity: Entity,
        coroutineScope: CoroutineScope,
        positionInRoot: MutableState<Offset>
    ) {
        if (entity is Tokenizable) {
            detectDragGestures(
                onDragStart = {
                    logger.debug { dependencies.state.tokenizableDragging.value }
                },
                onDragEnd = {
                    with(dependencies.eventHandler) {
                        with(dependencies.state) {
                            coroutineScope.launch {
                                recover({
                                    TokenPlacingRequested(
                                        game.bind().id,
                                        entity.id,
                                        scene.current.ensureNotNull().bind().id,
                                        (tokenizableDragging.ensureNotNull()
                                            .bind().offset - scene.foregroundImage.offset.ensureNotNull()
                                            .bind()).div(scene.foregroundImage.scale.value).toPoint()
                                    ).handle().bind()
                                }) {
                                    logger.error { it }
                                }

                                dependencies.state.tokenizableDragging.value = null
                                logger.debug { dependencies.state.tokenizableDragging.value }
                            }
                        }
                    }
                }
            ) { change, _ ->
                change.consume()
                dependencies.state.tokenizableDragging.value =
                    TokenizableDragging(entity, positionInRoot.value + change.position)

                logger.debug { dependencies.state.tokenizableDragging.value }
            }
        }
    }


}

fun Offset.toPoint(): Point = Point(x.roundToInt(), y.roundToInt())
