package tabletop.client.error

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler
import tabletop.common.error.TerminalErrorHandler
import kotlin.coroutines.CoroutineContext


class UIErrorHandler(
    private val userInterface: UserInterface,
    private val terminalErrorHandler: TerminalErrorHandler
) : ErrorHandler<CommonError>, CoroutineScope {

    private val logger = KotlinLogging.logger { }

    override suspend fun CommonError.handle() {
        with(terminalErrorHandler) { handle() }

        with(userInterface) {
            connectionScreenModel.await().errors.value += this@handle
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext = job
}

