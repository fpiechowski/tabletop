package tabletop.client.command

import arrow.core.raise.either
import arrow.core.raise.recover
import arrow.fx.stm.atomically
import tabletop.client.di.DependenciesAdapter
import tabletop.client.event.UserAuthenticated
import tabletop.common.command.Command
import tabletop.common.command.GetGameCommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.command.SignInCommandResult
import tabletop.common.error.CommonError


class CommandResultExecutor(
    private val dependencies: DependenciesAdapter
) {
    private val state by lazy { dependencies.state }
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }
    private val eventHandler by lazy { dependencies.eventHandler }

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    suspend fun Command.Result<*, *>.execute() = either {
        recover<CommonError, Unit>({
            when (this@execute) {
                is GetGameCommandResult -> atomically { state.game.put(this@execute.data) }
                is GetGamesCommandResult -> atomically { state.gameListing.put(this@execute.data) }
                is SignInCommandResult -> {
                    atomically { state.user.put(data) }
                    with(eventHandler) { UserAuthenticated(data).handle().bind() }
                }

                else -> {}
            }
        }) {
            raise(Command.Result.Error("Error when executing $this", it))
        }
    }
}