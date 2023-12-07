package tabletop.client.game

import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput

@ExperimentalComposeUiApi
actual fun Modifier.sceneViewControlModifier(
    scaleState: MutableState<Float>,
    positionState: MutableState<Offset>,
    transformableState: TransformableState
): Modifier =
    onPointerEvent(PointerEventType.Scroll) {
        if (it.changes.first().scrollDelta.y > 0) {
            scaleState.value *= 1.1f
        } else {
            scaleState.value *= 0.9f
        }
    }.pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            positionState.value += dragAmount
        }
    }.transformable(transformableState)