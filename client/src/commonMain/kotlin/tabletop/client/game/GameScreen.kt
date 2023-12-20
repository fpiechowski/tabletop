package tabletop.client.game

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import arrow.core.raise.recover
import cafe.adriel.voyager.core.screen.Screen
import com.seiko.imageloader.LocalImageLoader
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.di.Dependencies
import tabletop.client.generateImageLoader
import tabletop.client.io.loadImageFile
import tabletop.client.ui.AsyncImage
import tabletop.client.ui.Window
import kotlin.math.roundToInt

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class GameScreen(
    private val dependencies: Dependencies
) : Screen {
    private val logger = KotlinLogging.logger { }

    private val library: Library = Library(dependencies)

    @Composable
    override fun Content() {
        val windowsOpened by dependencies.userInterface.openedWindows.collectAsState()

        dependencies.uiErrorHandler.errorDialog()

        Box(modifier = Modifier.fillMaxSize()) {
            SceneView()

            with(library) {
                LibraryButton(Modifier.align(Alignment.TopStart).padding(16.dp))
            }

            windowsOpened.forEach { (_, it) ->
                Window(it.title, it.modifier, dependencies.userInterface, it.offsetState, it.id) {
                    it.content()
                }
            }
        }
    }


    @Composable
    fun SceneView() {
        val currentScene by dependencies.state.scene.current.collectAsState()

        val offsetState = remember { mutableStateOf(Offset.Zero) }

        val sceneForegroundImageScale = dependencies.state.scene.foregroundImage.scale
        val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->
            sceneForegroundImageScale.value *= zoomChange
        }

        val connectionDependencies by dependencies.state.connectionDependencies.collectAsState()
        val selectedToken by dependencies.state.scene.selectedToken.collectAsState()

        val zoom by sceneForegroundImageScale.collectAsState()

        Box(
            modifier = Modifier.fillMaxSize()
                .border(1.dp, Color.Blue)
                .sceneViewControlModifier(sceneForegroundImageScale, offsetState, transformableState)
        ) {
            currentScene
                ?.let { scene ->
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            offsetState.value.x.roundToInt(),
                            offsetState.value.y.roundToInt()
                        )
                    }.scale(zoom)) {
                        CompositionLocalProvider(
                            LocalImageLoader provides remember { generateImageLoader() },
                        ) {
                            recover({
                                scene.foregroundImagePath?.let { foregroundPath ->
                                    AsyncImage(
                                        load = {
                                            loadImageFile(
                                                connectionDependencies!!.assets.assetFile(foregroundPath).bind()
                                            )
                                        },
                                        painterFor = { remember { BitmapPainter(it) } },
                                        contentDescription = "Foreground image",
                                        modifier = Modifier.onGloballyPositioned {
                                            dependencies.state.scene.foregroundImage.offset.value =
                                                it.positionInWindow()
                                                    .also { logger.debug { "Scene image window pos = $it" } }
                                        }
                                    )
                                }

                                scene.tokens.forEach { (id, token) ->
                                    Box(
                                        modifier = Modifier.offset { IntOffset(token.position.x, token.position.y) }
                                            .clickable { dependencies.state.scene.selectedToken.value = token }
                                            .let {
                                                if (token == selectedToken) {
                                                    it.border(1.dp, Color.Yellow)
                                                } else {
                                                    it
                                                }
                                            }
                                    ) {
                                        AsyncImage(
                                            load = {
                                                loadImageFile(
                                                    connectionDependencies!!.assets.assetFile(token.imageFilePath)
                                                        .bind()
                                                )
                                            },
                                            painterFor = { remember { BitmapPainter(it) } },
                                            contentDescription = "Token $id"
                                        )
                                    }
                                }
                            }) {
                                AlertDialog(onDismissRequest = {}) {
                                    Text(it.message ?: "Unknown error")
                                }
                            }
                        }
                    }

                    Text(
                        scene.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
                    )
                }


            val tokenizableDragging by dependencies.state.tokenizableDragging.collectAsState()

            tokenizableDragging?.let {
                recover({
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            it.offset.x.roundToInt(),
                            it.offset.y.roundToInt()
                        )
                    }.zIndex(10f).scale(zoom)) {
                        AsyncImage(
                            load = {
                                loadImageFile(
                                    connectionDependencies!!.assets.assetFile(it.tokenizable.tokenImageFilePath).bind()
                                )
                            },
                            painterFor = { remember { BitmapPainter(it) } },
                            contentDescription = "Token image",
                            modifier = Modifier.alpha(0.6f).zIndex(5f)
                                .border(1.dp, Color.Red)
                        )
                    }
                }) {
                    AlertDialog(onDismissRequest = {}) {
                        Text(it.message ?: "Unknown error")
                    }
                }
            }
        }
    }


    companion object {
        const val libraryWindowModelId = "5d4d1968-7fbd-4d67-9320-650c65671815"
    }
}

expect fun Modifier.sceneViewControlModifier(
    scaleState: MutableStateFlow<Float>,
    positionState: MutableState<Offset>,
    transformableState: TransformableState
): Modifier

fun Modifier.nonDesktopViewControlModifier(
    positionState: MutableState<Offset>,
    transformableState: TransformableState
) = pointerInput(Unit) {
    detectDragGestures { change, dragAmount ->
        positionState.value += dragAmount
    }
}.transformable(transformableState)