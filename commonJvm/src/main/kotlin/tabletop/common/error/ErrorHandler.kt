package tabletop.common.error

import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.common.di.CommonDependencies

fun interface ErrorHandler<T : CommonError> {
    suspend fun T.handle()
}

class ConnectionErrorHandler(private val dependencies: CommonDependencies.ConnectionScope) : ErrorHandler<CommonError> {
    override suspend fun CommonError.handle() = with(dependencies) {
        recover<CommonError, Unit>(block = {
            with(terminalErrorHandler) { this@handle.handle() }
            with(connectionCommunicator) { this@handle.send<CommonError>() }
        }, recover = {
            with(terminalErrorHandler) { this@handle.handle() }
        }, catch = {
            with(terminalErrorHandler) { CommonError.ThrowableError(it).handle() }
        })
    }

}

class TerminalErrorHandler : ErrorHandler<CommonError> {
    private val logger = KotlinLogging.logger { }

    override suspend fun CommonError.handle() {
        logger.error {
            """$this
                |${this.findThrowable()?.stackTrace ?: ""}
            """.trimMargin()
        }
    }
}

