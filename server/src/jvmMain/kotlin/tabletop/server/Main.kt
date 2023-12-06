package tabletop.server

import arrow.continuations.SuspendApp
import arrow.core.raise.fold
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.common.error.CommonError
import tabletop.server.di.Dependencies

private val logger = KotlinLogging.logger { }

fun main() = SuspendApp {
    val dependencies = Dependencies()

    with(dependencies.terminalErrorHandler) {
        fold<CommonError, Unit, Unit>(
            block = {
                dependencies.serverAdapter.launch()
                Unit
            },
            recover = { it.handle() },
            catch = { CommonError.ThrowableError(it).handle() },
            transform = { logger.info { "Exiting..." } }
        )
    }
}

