package tabletop.client.menu

import arrow.core.raise.recover
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTextButton
import ktx.scene2d.window
import tabletop.client.connection.logout
import tabletop.client.error.handleUI
import tabletop.client.ui.UserInterface
import tabletop.client.ui.confirmationDialog
import tabletop.common.connection.Connection

context (UserInterface, Connection)
fun connectionMenu() = scene2d.window("Session Menu") {
    visTextButton("Logout") {
        onClick {
            confirmationDialog({
                KtxAsync.launch {
                    recover({
                        logout()
                    }) {
                        it.handleUI(UserInterface)
                    }
                }
            })
        }
    }
}

