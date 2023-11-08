package tabletop.client.event

import korlibs.event.EventType
import tabletop.common.event.Event
import tabletop.common.event.GameLoaded
import korlibs.event.BEvent as KorgeEvent
import korlibs.event.TEvent as KorgeTypedEvent

abstract class UIEvent<T : KorgeEvent, E : Event>(
    open val event: Event
) :
    Event by event,
    KorgeTypedEvent<T> {

    override var target: Any? = null
}

class GameLoadedUIEvent(override val event: GameLoaded) : UIEvent<GameLoadedUIEvent, GameLoaded>(event) {
    companion object : EventType<GameLoadedUIEvent>

    override val type = GameLoadedUIEvent
}
