package tabletop.client.event

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import tabletop.common.error.CommonError
import tabletop.common.process.CommonChannel

abstract class Event {

    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()

    companion object

    class Channel : CommonChannel<Event>() {
        override val logger: KLogger = KotlinLogging.logger {}

        companion object
    }
}
