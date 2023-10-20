package tabletop.client.command

import arrow.core.raise.Raise
import arrow.core.raise.recover
import arrow.fx.stm.atomically
import tabletop.client.event.Event
import tabletop.client.event.UserAuthenticated
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.command.Command
import tabletop.common.command.GetGameCommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.command.SignInCommandResult
import tabletop.common.process.publish

context (Raise<Command.Result.Error>, UserInterface, State, Event.Processor)
@Suppress("UNCHECKED_CAST")
suspend fun <C : Command, T : Command.Result.Data> Command.Result<C, T>.execute(): Unit =
    recover({
        when (this@execute) {
            is GetGameCommandResult -> atomically { game.put(this@execute.data) }
            is GetGamesCommandResult -> atomically { gameListing.put(this@execute.data) }
            is SignInCommandResult -> {
                atomically { user.put(data) }
                UserAuthenticated(data).publish()
            }

            else -> {}
        }
    }) {
        raise(Command.Result.Error("Error when executing $this", it))
    }

