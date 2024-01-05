package tabletop.client.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

data class WindowModel(
    val title: String,
    val modifier: Modifier,
    val offsetState: MutableStateFlow<IntOffset>,
    val id: UUID,
    val content: @Composable () -> Unit,
)

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun Window(
    title: String,
    openedWindows: MutableStateFlow<Map<UUID, WindowModel>>,
    modifier: Modifier = Modifier,
    offsetState: MutableStateFlow<IntOffset> = MutableStateFlow(IntOffset.Zero),
    id: UUID = UUID.generateUUID(),
    content: @Composable () -> Unit,
) {
    val offset by offsetState.collectAsState()

    Card(
        Modifier
            .then(modifier)
            .offset { offset },
        elevation = CardDefaults.cardElevation(20.dp)
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
                        openedWindows.value -= id
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

        Row(horizontalArrangement = Arrangement.End) {
            Icon(Icons.Default.OpenInFull, contentDescription = "Resize Window")
        }
    }
}
