package tabletop.client.event

import tabletop.common.error.CommonError

abstract class Event {


    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object
}

abstract class ConnectionEvent : Event()
