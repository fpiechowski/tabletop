package tabletop.client.game

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.di.Dependencies

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class GameScreen(
    private val dependencies: Dependencies,
) {

    private val library: Library = Library(dependencies.childDependencies("library"))

    private val sceneView: SceneView =SceneView(dependencies.childDependencies("sceneView"))


    @Composable
    fun content() {
        val openedWindows = dependencies.windows.openedWindows.subscribeAsState()

        Box(modifier = Modifier.fillMaxSize()) {
            sceneView.content()
            library.button(Modifier.align(Alignment.TopStart).padding(16.dp))
            openedWindows.value.forEach { (_, it) ->
                it.content()
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
    detectDragGestures { _, dragAmount ->
        positionState.value += dragAmount
    }
}.transformable(transformableState)