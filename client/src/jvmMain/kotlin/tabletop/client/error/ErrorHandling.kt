package tabletop.client.error

import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError
import tabletop.common.error.handleTerminal


context (UserInterface)
suspend fun CommonError.handleUI(source: Any) {
    handleTerminal(source)
    errorDialog(this).show(main.await().currentStageScreen.stage)
}
