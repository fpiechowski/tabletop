package tabletop.server

import arrow.continuations.SuspendApp
import arrow.core.raise.fold
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.common.error.CommonError
import tabletop.server.di.DependenciesAdapter


private val logger = KotlinLogging.logger { }

fun main() = SuspendApp {
    val dependenciesAdapter = DependenciesAdapter()

    with(dependenciesAdapter.terminalErrorHandler) {
        fold<CommonError, Unit, Unit>(
            block = {
                dependenciesAdapter.serverAdapter.launch()
            },
            recover = { it.handle() },
            catch = { CommonError.ThrowableError(it).handle() },
            transform = { logger.info { "Exiting..." } }
        )
    }
}

