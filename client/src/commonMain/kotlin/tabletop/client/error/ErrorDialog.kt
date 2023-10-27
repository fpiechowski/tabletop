package tabletop.client.error

import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError

fun <T : CommonError> UserInterface.errorDialog(error: T): Any = TODO(
    """
        scene2d.dialog(error::class.qualifiedName ?: "CommonError") {
            debug = false


            contentTable.add(TextArea(error.toString(), skin)).fill().expand()
            background.minWidth = 500f
            background.minHeight = 500f
            buttonTable.add(
                VisTextButton("Close").apply {
                    onClick {
                        this@dialog.hide()
                    }
                }
            )
        }
    """.trimIndent()
)