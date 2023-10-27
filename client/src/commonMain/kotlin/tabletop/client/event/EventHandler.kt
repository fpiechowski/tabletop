package tabletop.client.event

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import kotlinx.coroutines.launch
import tabletop.client.di.DependenciesAdapter
import tabletop.client.server.ServerAdapter
import tabletop.common.command.Command
import tabletop.common.error.CommonError

class EventHandler(
    private val dependencies: DependenciesAdapter
) {
    private val commandChannel by lazy { dependencies.commandChannel }
    private val mainCoroutineScope by lazy { dependencies.mainCoroutineScope }
    private val uiErrorHandler by lazy { dependencies.uiErrorHandler }

    suspend fun <T : Event> T.handle(): Either<Event.Error, Unit> =
        either {
            with(commandChannel) {
                recover<CommonError, Unit>({
                    when (this@handle) {
                        is UserAuthenticated ->
                            Command.GetGames(user.id)
                                .publish()

                        is LoadingGameAttempted -> Command.GetGame(gameListingItem.id).publish().bind()

                        is ConnectionAttempted ->
                            mainCoroutineScope.launch {
                                recover({
                                    ServerAdapter(dependencies).connect(host, port, credentialsData).bind()
                                }) {
                                    with(uiErrorHandler) { it.handle() }
                                }
                            }

                        else -> {}
                    }
                }) {
                    raise(Event.Error("Error when processing $this", it))
                }
            }
        }

}