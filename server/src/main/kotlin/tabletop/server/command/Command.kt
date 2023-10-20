package tabletop.server.command

import arrow.core.raise.Raise
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import tabletop.common.Game
import tabletop.common.auth.Authentication
import tabletop.common.command.Command
import tabletop.common.command.GetGameCommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.command.SignInCommandResult
import tabletop.common.error.CommonError
import tabletop.server.ServerAdapter
import tabletop.server.auth.authenticate
import tabletop.server.persistence.Persistence
import tabletop.server.persistence.retrieve

context (Raise<Command.Error>, ServerAdapter, Persistence, Authentication)
@Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
fun <C : Command> C.process(): Command.Result<C, *> =
    recover({
        when (this@process) {
            is Command.GetGames -> execute()
            is Command.GetGame -> execute()
            is Command.SignIn -> execute()
            else -> raise(Command.Error("Unhandled Command type ${this::class.simpleName}"))
        } as Command.Result<C, *>
    }) {
        raise(Command.Error("Error on executing $this", it))
    }


context (Raise<CommonError>, ServerAdapter, Command, Persistence)
private fun Command.GetGames.execute(): GetGamesCommandResult =
    retrieve { games.values.filter { it.players.any { it.user.id == userId } } }
        .let {
            val listing = Game.Listing(it.map { Game.Listing.Item(it) })
            GetGamesCommandResult(this, listing)
        }

context (Raise<CommonError>, ServerAdapter, Command, Persistence)
private fun Command.GetGame.execute(): GetGameCommandResult {
    val game = ensureNotNull(retrieve { games[gameId] }) { Command.Error("Game not found for ID $gameId") }
    return GetGameCommandResult(this, game)
}

context (Raise<CommonError>, ServerAdapter, Command, Persistence, Authentication)
private fun Command.SignIn.execute(): SignInCommandResult {
    val user = authenticate(principal, secret)
    return SignInCommandResult(this, user)
}
