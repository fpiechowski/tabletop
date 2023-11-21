package tabletop.client.ui

import dev.fritz2.core.*
import dev.fritz2.headless.components.inputField
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

fun RenderContext.textField(
    label: String,
    value: Store<String>,
    type: String = "text",
): Tag<HTMLDivElement> = inputField {
    value(value)

    div("textfield flex gap-0 max-w-sm") {
        div("textfield-left shrink") {}
        div("textfield-center grow relative flex justify-center items-center") {
            inputLabel("absolute top-1 left-1") {
                +label
            }
            inputTextfield("w-5/6 h-5/6 bg-transparent border-none focus:outline-none text-center") {
                type(type)
            }
        }
        div("textfield-right shrink") {}
    }
}
