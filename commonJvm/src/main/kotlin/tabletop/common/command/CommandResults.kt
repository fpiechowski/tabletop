package tabletop.common.command

import kotlinx.serialization.Serializable
import tabletop.common.Game
import tabletop.common.user.User


@Serializable
class GetGamesCommandResult(override val command: Command.GetGames, override val data: Game.Listing) :
    Command.Result<Command.GetGames, Game.Listing>() {
    override val shared: Boolean = false
}

@Serializable
class GetGameCommandResult(override val command: Command.GetGame, override val data: Game) :
    Command.Result<Command.GetGame, Game>() {
    override val shared: Boolean = false
}

@Serializable
class SignInCommandResult(override val command: Command.SignIn, override val data: User) :
    Command.Result<Command.SignIn, User>() {
    override val shared: Boolean = false
}