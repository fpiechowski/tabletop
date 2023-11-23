package tabletop.client.ui

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import org.w3c.dom.HTMLDivElement

fun RenderContext.customButton(text: String, classes: String = "", block: HtmlTag<HTMLDivElement>.() -> Unit) =
    Button(text, classes, block).run { render() }

class Button(
    private val text: String,
    private val classes: String,
    private val block: HtmlTag<HTMLDivElement>.() -> Unit = {}
) {
    fun RenderContext.render() {
        div("button inline-flex gap-0 hover:cursor-pointer text-white $classes") {
            block()
            div("button-left shrink") {}
            div("button-center text-xl flex items-center px-5") {
                +text
            }
            div("button-right shrink") {}
        }
    }
}