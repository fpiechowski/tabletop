package tabletop.client.game

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.seiko.imageloader.LocalImageLoader
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.client.di.Dependencies
import tabletop.client.generateImageLoader
import tabletop.client.io.loadImageFile
import tabletop.client.ui.AsyncImage
import kotlin.math.roundToInt

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class SceneView(
    val dependencies: Dependencies
) {
    private val logger = KotlinLogging.logger {}

    @Composable
    fun content() {
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
                            arrow.core.raise.recover({
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
                arrow.core.raise.recover({
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
}