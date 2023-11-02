package tabletop.client.event

import korlibs.event.EventType
import tabletop.common.error.CommonError
import korlibs.event.BEvent as KorgeEvent
import korlibs.event.TEvent as KorgeTypedEvent

abstract class Event {


    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object
}

abstract class UIEvent<T : KorgeEvent>(override val type: EventType<T>) : Event(), KorgeTypedEvent<T> {
    override var target: Any? = null
}