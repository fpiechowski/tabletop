package tabletop.client.ui

import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.Serializable
import tabletop.client.Main
import tabletop.client.event.Event
import tabletop.client.input.Input
import tabletop.common.error.CommonError
import tabletop.common.serialization.Serialization


context (Serialization, Input, Event.Processor, tabletop.client.state.State)
class UserInterface {
    val main = CompletableDeferred<Main>()

    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}

