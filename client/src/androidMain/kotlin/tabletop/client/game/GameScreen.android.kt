package tabletop.client.game

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

actual fun Modifier.sceneViewControlModifier(
    scaleState: MutableState<Float>,
    positionState: MutableState<Offset>,
    transformableState: TransformableState
): Modifier =
    nonDesktopViewControlModifier(positionState, transformableState)