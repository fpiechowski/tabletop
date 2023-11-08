package tabletop.client.error

import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.render.alert
import tabletop.client.di.Dependencies
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler


class UIErrorHandler(
    private val dependencies: Dependencies
) : ErrorHandler<CommonError> {
    private val logger = KotlinLogging.logger { }

    override suspend fun CommonError.handle() = with(dependencies) {
        with(terminalErrorHandler) { handle() }

        with(userInterface) {
            stage.await().gameWindow.alert(this@handle.toString())
        }
    }
}
