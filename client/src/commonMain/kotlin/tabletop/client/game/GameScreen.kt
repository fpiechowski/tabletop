package tabletop.client.game

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.seiko.imageloader.LocalImageLoader
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.client.di.Dependencies
import tabletop.client.generateImageLoader
import tabletop.client.io.loadImageFile
import tabletop.client.ui.AsyncImage
import tabletop.client.ui.Window
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
class GameScreen(
    private val dependencies: Dependencies
) : Screen {
    private val logger = KotlinLogging.logger { }

    val library: Library = Library(dependencies)

    @Composable
    override fun Content() {
        val windowsOpened by dependencies.userInterface.openedWindows.collectAsState()

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
        val currentScene by dependencies.state.currentScene.collectAsState()

        val positionState = remember { mutableStateOf(Offset.Zero) }
        val scaleState = remember { mutableStateOf(1f) }
        val transformableState = rememberTransformableState { zoomChange, panChange, rotationChange ->
            scaleState.value *= zoomChange
        }

        val connectionDependencies by dependencies.state.connectionDependencies.collectAsState()

        Box(
            modifier = Modifier.fillMaxSize()
                .sceneViewControlModifier(scaleState, positionState, transformableState)
        ) {

            currentScene
                ?.let {
                    Box(modifier = Modifier.offset {
                        IntOffset(
                            positionState.value.x.roundToInt(),
                            positionState.value.y.roundToInt()
                        )
                    }.scale(scaleState.value)) {
                        CompositionLocalProvider(
                            LocalImageLoader provides remember { generateImageLoader() },
                        ) {
                            it.foregroundImagePath?.let { foregroundPath ->
                                AsyncImage(
                                    load = {
                                        loadImageFile(connectionDependencies!!.assets.assetFile(foregroundPath))
                                    },
                                    painterFor = { remember { BitmapPainter(it) } },
                                    contentDescription = "Foreground image"
                                )
                            }
                        }
                    }

                    Text(
                        it.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
                    )
                }
        }
    }


    companion object {
        const val libraryWindowModelId = "5d4d1968-7fbd-4d67-9320-650c65671815"
    }
}

expect fun Modifier.sceneViewControlModifier(
    scaleState: MutableState<Float>,
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