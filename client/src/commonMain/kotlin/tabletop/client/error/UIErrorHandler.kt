package tabletop.client.error

import korlibs.render.alert
import tabletop.client.di.DependenciesAdapter
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler


class UIErrorHandler(
    private val dependencies: DependenciesAdapter
) : ErrorHandler<CommonError> {
    override suspend fun CommonError.handle() = with(dependencies) {
        with(terminalErrorHandler) { handle() }

        with(userInterface) {
            stage.await().gameWindow.alert(this@handle.toString())
        }
    }
}
