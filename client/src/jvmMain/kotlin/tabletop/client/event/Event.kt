package tabletop.client.event

import arrow.core.raise.Raise
import arrow.core.raise.recover
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import ktx.async.KtxAsync
import tabletop.client.error.handleUI
import tabletop.client.input.Input
import tabletop.client.server.ServerAdapter
import tabletop.client.server.connect
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.ChannelProcessor
import tabletop.common.command.Command
import tabletop.common.error.CommonError
import tabletop.common.publish
import tabletop.common.serialization.Serialization

abstract class Event {
    val timestamp: Instant = Clock.System.now()

    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()

    companion object

    class Processor : ChannelProcessor<Event>() {
        companion object
    }
}

context (CoroutineScope, Raise<Event.Error>, Command.Processor, Event.Processor, Serialization, Input, UserInterface, State)
suspend fun Event.process() = recover({
    when (this@process) {
        is UserAuthenticated ->
            Command.GetGames(user.id)
                .publish()

        is LoadingGameAttempted -> Command.GetGame(gameListingItem.id).publish()
        is ConnectionAttempted -> KtxAsync.launch {
            recover({ ServerAdapter(host, port).connect(credentialsData) }) {
                it.handleUI(UserInterface)
            }
        }

        else -> {}
    }
}) {
    raise(Event.Error("Error when processing $this", it))
}