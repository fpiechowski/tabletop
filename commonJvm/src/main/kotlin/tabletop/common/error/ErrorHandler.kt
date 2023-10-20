package tabletop.common.error

import arrow.core.raise.fold
import tabletop.common.connection.Connection
import tabletop.common.connection.send
import tabletop.common.logging.logger
import tabletop.common.serialization.Serialization


context (Connection, Serialization)
suspend inline fun <reified T : CommonError> T.handleConnection(source: Any) = fold(block = {
    handleTerminal(source)
    (this@handleConnection as CommonError).send<CommonError>()
}, recover = {
    it.handleTerminal(source)
}, catch = {
    CommonError.ThrowableError(it).handleTerminal(source)
}, transform = { })

fun CommonError.handleTerminal(source: Any) {
    source.logger.error {
        """$this${findThrowable()?.stackTrace?.let { "\n$it" } ?: ""}
    """.trimMargin()
    }
}

private fun CommonError.findThrowable(): CommonError.ThrowableError? = when {
    this is CommonError.ThrowableError -> this
    else -> cause?.findThrowable()
}