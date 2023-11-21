package tabletop.client.error

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import tabletop.client.ui.Notification
import tabletop.client.ui.UserInterface
import tabletop.client.update
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
            notifications.notifications.update {
                it + Notification(
                    this@handle.message ?: (this::class.simpleName ?: "Unknown Error"), Notification.Type.Error
                )
            }
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext = job
}

