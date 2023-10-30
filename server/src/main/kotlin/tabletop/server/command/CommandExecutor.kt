package tabletop.server.command

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.core.raise.recover
import tabletop.common.Game
import tabletop.common.command.Command
import tabletop.common.command.GetGameCommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.command.SignInCommandResult
import tabletop.common.error.CommonError
import tabletop.server.di.DependenciesAdapter

class CommandExecutor(
    private val connectionScopeDependencies: DependenciesAdapter.ConnectionScope,
) {
    private val persistence by lazy { connectionScopeDependencies.persistence }
    private val authentication by lazy { connectionScopeDependencies.authentication }
    private val connection by lazy { connectionScopeDependencies.connection }

    suspend fun Command.execute(): Either<Command.Error, Command.Result<*, *>> =
        either {
            recover({
                when (this@execute) {
                    is Command.GetGames -> execute().bind()
                    is Command.GetGame -> execute().bind()
                    is Command.SignIn -> execute().bind()

                    else -> raise(Command.Error("Unhandled Command type ${this::class.simpleName}", null))
                }
            }) {
                raise(Command.Error("Error on executing ${this@execute}", it))
            }
        }

    private fun Command.GetGames.execute(): Either<CommonError, GetGamesCommandResult> =
        either {
            val games = persistence.retrieve {
                games.values.filter { it.gameMaster.user.id == userId } +
                        games.values.filter { it.players.any { it.user.id == userId } }
                            .toSet()
            }.bind()
            val listing = Game.Listing(games.map { Game.Listing.Item(it) })
            GetGamesCommandResult(this@execute, listing)
        }

    private fun Command.GetGame.execute(): Either<CommonError, GetGameCommandResult> =
        either {
            val game = ensureNotNull(persistence.retrieve { games[gameId] }
                .bind()) { Command.Error("Game not found for ID $gameId", null) }
            GetGameCommandResult(this@execute, game)
        }

    private suspend fun Command.SignIn.execute(): Either<CommonError, SignInCommandResult> =
        either {
            val user = authentication.authenticate(principal, secret).bind()
            SignInCommandResult(this@execute, user)
        }

}
