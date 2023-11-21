package tabletop.client.ui

import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.Tag
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.w3c.dom.HTMLDivElement
import tabletop.common.geometry.Point

class Window(
    val title: String,
    val content: Tag<HTMLDivElement>.() -> Unit,
    val position: Store<Point> = storeOf(Point(0, 0), Job()),
    val dragging: Store<Boolean> = storeOf(false, Job()),
) {

    fun RenderContext.render(): Tag<HTMLDivElement> =
        div("window grid gap-0") {
            inlineStyle("position: absolute; left: ${position.current.x}px; top: ${position.current.y}px;")
            inlineStyle("grid-template-columns: auto 1fr auto; grid-template-rows: auto 1fr auto;")
            position.data.render {
                div("window-top-left window-corner justify-self-end align-self-end") { }
                div("window-top window-edge-horizontal align-self-end") { }
                div("window-top-right window-corner") { }
                div("window-left window-edge-vertical justify-self-end") { }
                div("window-center text-white") {
                    div("window-title flex justify-center items-center") {
                        dragging()
                        h1("text-2xl") { +title }
                    }
                    div("content p-2") {
                        content()
                    }
                }
                div("window-right window-edge-vertical justify-self-start") { }
                div("window-bottom-left window-corner justify-self-end") { }
                div("window-bottom window-edge-horizontal align-self-start") { }
                div("window-bottom-right window-corner") { }
            }
        }


    private fun Tag<*>.dragging() {
        var lastMousePosition: Point? = null

        mousedowns
            .onEach { console.log("$it position=${position.current}; dragging=${dragging.current}; lastMousePosition=$lastMousePosition") }
            .map { it.buttons == 1.toShort() }
            .handledBy(dragging.update)

        mouseups
            .onEach { console.log("$it position=${position.current}; dragging=${dragging.current}; lastMousePosition=$lastMousePosition") }
            .filter { it.button == 1.toShort() }
            .map { false }
            .handledBy(dragging.update)

        mousemoves
            .onEach { lastMousePosition = Point(it.clientX, it.clientY) }
            .filter { dragging.current }
            .onEach { console.log("$it position=${position.current}; dragging=${dragging.current}; lastMousePosition=$lastMousePosition") }
            .map { position.current + Point(it.clientX - lastMousePosition!!.x, it.clientY - lastMousePosition!!.y) }
            .onEach { console.log("new position=$it") }
            .handledBy(position.update)
    }
}

fun RenderContext.window(
    title: String,
    content: Tag<HTMLDivElement>.() -> Unit
): Tag<HTMLDivElement> =
    Window(title, content).run {
        render()
    }
