package tabletop.client.error

import tabletop.client.di.DependenciesAdapter
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler


class UIErrorHandler(
    val dependencies: DependenciesAdapter
) : ErrorHandler<CommonError> {
    override suspend fun CommonError.handle() = with(dependencies) {
        with(terminalErrorHandler) { handle() }
        with(userInterface) {
            TODO(
                """
        errorDialog(this@ErrorHandler).show(main.await().currentStageScreen.stage)
    """.trimIndent()
            )
        }
    }
}


suspend fun <T : CommonError> T.uiErrorHandler(
    source: Any,
    terminalErrorHandler: ErrorHandler<T>,
    userInterface: UserInterface
) = ErrorHandler<T> {

}
