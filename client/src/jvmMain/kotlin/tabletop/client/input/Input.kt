package tabletop.client.input

import arrow.core.raise.recover
import com.badlogic.gdx.Input.Keys
import kotlinx.coroutines.launch
import ktx.app.KtxInputAdapter
import ktx.async.KtxAsync
import tabletop.client.error.handleUI
import tabletop.client.event.EscapeKeyDown
import tabletop.client.event.Event
import tabletop.client.ui.UserInterface
import tabletop.common.publish


object Input

context (Event.Processor, UserInterface)
internal class InputAdapter : KtxInputAdapter {
    override fun keyDown(keycode: Int): Boolean {
        KtxAsync.launch {
            recover({
                when (keycode) {
                    Keys.ESCAPE -> EscapeKeyDown.publish()
                }
            }) {
                it.handleUI(this)
            }
        }

        return false
    }
}