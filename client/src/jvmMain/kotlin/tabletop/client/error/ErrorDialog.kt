package tabletop.client.error

import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.kotcrab.vis.ui.widget.VisTextButton
import ktx.actors.onClick
import ktx.scene2d.dialog
import ktx.scene2d.scene2d
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError

context (UserInterface)
fun <T : CommonError> errorDialog(error: T) = scene2d.dialog(error::class.qualifiedName ?: "CommonError") {
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