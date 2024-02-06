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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.client.di.Dependencies
import tabletop.shared.entity.Identifiable

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
data class Window(
    val dependencies: Dependencies,
    val title: String,
    val modifier: Modifier,
    val position: MutableValue<IntOffset> = MutableValue(IntOffset.Zero),
    override val id: UUID = UUID.generateUUID(),
    val content: @Composable () -> Unit,
) : Identifiable<UUID> {
    @ExperimentalLayoutApi
    @ExperimentalMaterial3Api
    @ExperimentalComposeUiApi
    @Composable
    fun content() {
        val position = position.subscribeAsState()

        Card(
            Modifier
                .then(modifier)
                .offset { position.value },
            elevation = CardDefaults.cardElevation(20.dp)
        ) {
            Column {
                Box(
                    Modifier.fillMaxWidth()
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                this@Window.position.apply {
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
                            dependencies.windows.openedWindows.value -= id
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
}

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class Windows(val dependencies: Dependencies) {
    val openedWindows: MutableValue<Map<UUID, Window>> = MutableValue(mapOf())
}


