package tabletop.client.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.client.di.Dependencies

data class WindowModel(
    val title: String,
    val modifier: Modifier,
    val offsetState: MutableStateFlow<IntOffset>,
    val id: UUID,
    val content: @Composable () -> Unit,
)

@Composable
fun Window(
    title: String, modifier: Modifier,
    userInterface: UserInterface,
    offsetState: MutableStateFlow<IntOffset>,
    id: UUID,
    content: @Composable () -> Unit,
) {
    val offset by offsetState.collectAsState()

    Card(
        Modifier
            .then(modifier)
            .height(300.dp)
            .offset { offset }
    ) {
        Column {
            Box(
                Modifier.fillMaxWidth()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetState.apply {
                                value += dragAmount.round()
                            }
                        }
                    }
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center).padding(8.dp)
                )

                Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                    IconButton(onClick = {
                        userInterface.openedWindows.value -= id
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            content()
        }
    }
}
