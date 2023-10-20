package tabletop.server

import arrow.continuations.SuspendApp
import arrow.core.raise.fold
import tabletop.common.auth.Authentication
import tabletop.common.error.CommonError
import tabletop.common.error.handleTerminal
import tabletop.common.logging.logger
import tabletop.common.serialization.Serialization
import tabletop.server.demo.storeDemoEntities
import tabletop.server.persistence.Persistence
import tabletop.server.serialization.buildSerializersModule

object Main

fun main() = SuspendApp {
    fold(
        block = {
            with(Persistence) {
                storeDemoEntities()

                with(Authentication) {
                    with(Serialization { buildSerializersModule() }) {
                        with(ServerAdapter()) {
                            start()
                        }
                    }
                }
            }
        },
        recover = { it.handleTerminal(Main) },
        catch = { CommonError.ThrowableError(it).handleTerminal(Main) },
        transform = { logger.info { "Exiting..." } }
    )
}

