package tabletop.client.error

import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.render.alert
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler
import tabletop.common.error.TerminalErrorHandler


class UIErrorHandler(
    private val userInterface: UserInterface,
    private val terminalErrorHandler: TerminalErrorHandler
) : ErrorHandler<CommonError> {
    private val logger = KotlinLogging.logger { }

    override suspend fun CommonError.handle() {
        with(terminalErrorHandler) { handle() }

        with(userInterface) {
            stage.await().gameWindow.alert(this@handle.toString())
        }
    }
}

