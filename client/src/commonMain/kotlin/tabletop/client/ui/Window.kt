package tabletop.client.ui

import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import tabletop.common.geometry.Point

class DraggableWindow(
    val visible: Store<Boolean> = storeOf(true, Job()),
    val position: Store<Point> = storeOf(Point(0, 0), Job()),
    val dragging: Store<Boolean> = storeOf(false, Job())
)

fun RenderContext.draggableWindow(title: String, content: RenderContext.(DraggableWindow) -> Unit) =
    DraggableWindow().run {
        visible.data.render {
            if (it) {
                div {
                    div {
                        +title

                        button {
                            +"Close"
                            clicks.map { false } handledBy visible.update
                        }

                        mousedowns.map { true } handledBy dragging.update
                    }

                    div {
                        content(this@run)
                    }

                    mousemoves
                        .filter { dragging.current }
                        .map { event ->
                            Point(event.clientX, event.clientY)
                        } handledBy position.update

                    mouseups.map { false } handledBy dragging.update

                    position.data.map { (x, y) ->
                        "position: absolute; left: ${x}px; top: ${y}px;"
                    } handledBy ::inlineStyle
                }
            }
        }
    }

