package tabletop.client.di

import dev.fritz2.routing.Router
import dev.fritz2.routing.routerOf
import kotlinx.coroutines.CompletableDeferred
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.error.CommonError
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization

class Dependencies(
    val router: Router<String> = routerOf("connection"),
    override val serialization: Serialization = Serialization(),
    override val terminalErrorHandler: TerminalErrorHandler = TerminalErrorHandler(),
    val state: State = State(),
) : CommonDependencies {
    val userInterface: UserInterface = UserInterface(this)
    val uiErrorHandler: UIErrorHandler = UIErrorHandler(userInterface, terminalErrorHandler)
    val eventHandler: EventHandler = EventHandler(this, userInterface, state, uiErrorHandler)
    val connectionDependenciesFactory = ConnectionDependencies.Factory { connection ->
        ConnectionDependencies(this, connection)
            .also { state.connectionDependencies.value = it }
    }

    init {
        if (!instance.isCompleted) {
            instance.complete(this)
        } else {
            with(terminalErrorHandler) {
                Error("${Dependencies::class.simpleName} instance already completed", null)
                    .handleSync()
            }
        }
    }

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object {
        val instance: CompletableDeferred<Dependencies> = CompletableDeferred()
    }
}

class ConnectionDependencies(
    dependencies: Dependencies,
    override val connection: Connection,
    override val connectionCommunicator: ConnectionCommunicator =
        ConnectionCommunicator(connection, dependencies.serialization),
    override val connectionErrorHandler: ConnectionErrorHandler = ConnectionErrorHandler(
        dependencies.terminalErrorHandler,
        connectionCommunicator
    ),
) : CommonDependencies.ConnectionScope {

    fun interface Factory {
        operator fun invoke(connection: Connection): ConnectionDependencies
    }
}



