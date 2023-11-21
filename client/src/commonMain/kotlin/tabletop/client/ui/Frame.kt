package tabletop.client.ui

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.Tag
import org.w3c.dom.HTMLDivElement

fun Tag<*>.frame(content: HtmlTag<HTMLDivElement>.() -> Unit) = div("bg-stone-700 border-1 border-stone-500 box-border p-3") {
    content()
}

fun Tag<*>.importantFrame(content: HtmlTag<HTMLDivElement>.() -> Unit) = div("bg-red-950 border-2 border-red-900 p-3 text-slate-100 ") {
    content()
}