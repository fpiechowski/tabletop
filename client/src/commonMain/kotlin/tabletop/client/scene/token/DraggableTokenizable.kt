package tabletop.client.scene.token

import dev.fritz2.core.RenderContext
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import org.w3c.dom.events.MouseEvent
import tabletop.client.di.Dependencies
import tabletop.client.scene.currentScene
import tabletop.common.event.TokenPlacingRequested
import tabletop.common.geometry.Point
import tabletop.common.scene.token.Tokenizable

suspend fun RenderContext.draggableTokenizable(
    tokenizable: Tokenizable,
    mouseEvent: MouseEvent,
    dependencies: Dependencies
) =
    DraggableTokenizable(Point(mouseEvent.x.toInt(), mouseEvent.y.toInt()), dependencies).run { render(tokenizable) }

class DraggableTokenizable(
    val position: Point,
    val dependencies: Dependencies
) {

    val draggingStore = storeOf(false, Job())
    val positionStore = storeOf(position, Job())

    suspend fun RenderContext.render(tokenizable: Tokenizable) {
        positionStore.data.render { position ->
            div {
                inlineStyle(
                    """
                position: absolute;
                left: ${position.x}px;
                top: ${position.y}px;
            """.trimIndent()
                )
                img {
                    src(tokenizable.tokenImageFilePath)
                    inlineStyle("transform: scale(${dependencies.userInterface.gameScreen.current.zoomScaleStore.current})")
                }


                attr("draggable", "true")

                mousedowns.map { it.button == 2.toShort() } handledBy draggingStore.update

                mouseups handledBy { mouseEvent ->
                    val dragging = mouseEvent.button == 2.toShort()

                    draggingStore.update(dragging)

                    if (!dragging) {
                        with(dependencies.eventHandler) {
                            currentScene()?.let { scene ->
                                val tokensDiv = dependencies.userInterface.gameScreen.current.sceneView.tokensDiv.await()
                                val rect = tokensDiv.domNode.getBoundingClientRect()
                                val localX = mouseEvent.clientX - rect.left
                                val localY = mouseEvent.clientY - rect.top
                                dependencies.state.maybeGame.current?.let {
                                    TokenPlacingRequested(
                                        it.id,
                                        tokenizable.id,
                                        scene.id,
                                        Point(localX.toInt(), localY.toInt())
                                    ).handle()
                                }
                            }
                        }
                    }
                }

                mousemoves handledBy {
                    if (it.button == 2.toShort()) {
                        positionStore.update(Point(it.x.toInt(), it.y.toInt()))
                    }
                }
            }
        }
    }
}