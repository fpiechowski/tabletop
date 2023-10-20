package tabletop.client.ui

import ktx.actors.onClick
import ktx.scene2d.dialog
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTextButton

context (UserInterface)
fun confirmationDialog(onConfirmed: () -> Unit, onRejected: () -> Unit = {}) =
    scene2d.dialog("Are you sure?") {
        visTextButton("Yes") {
            onClick {
                onConfirmed()
                this@dialog.remove()
            }
        }

        visTextButton("No") {
            onClick {
                onRejected()
                this@dialog.remove()
            }
        }
    }