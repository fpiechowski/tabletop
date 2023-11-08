package tabletop.common.error

import arrow.core.raise.Raise
import arrow.core.raise.recover
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies

fun interface ErrorHandler<T : CommonError> {
    suspend fun T.handle()

    companion object {
        suspend fun <T, E : ErrorHandler<CommonError>> E.use(block: suspend Raise<CommonError>.() -> T) = recover(
            { block() },
            recover = {
                it.handle()
            }, catch = {
                CommonError.ThrowableError(it).handle()
            })
    }
}

class ConnectionErrorHandler(private val dependencies: CommonDependencies.ConnectionScope) :
    ErrorHandler<CommonError>,
    ConnectionCommunicator.Aware {

    override suspend fun CommonError.handle() = with(dependencies) {
        recover<CommonError, Unit>(block = {
            with(terminalErrorHandler) { this@handle.handle() }
            with(connectionCommunicator) { this@handle.send<CommonError>().bind() }
        }, recover = {
            with(terminalErrorHandler) {
                it.handle()
            }
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

