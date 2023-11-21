package tabletop.client.di

import dev.fritz2.routing.Router
import dev.fritz2.routing.routerOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.entity.StateEntityGraph
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.entity.EntityGraph
import tabletop.common.error.CommonError
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization
import tabletop.common.user.User

class Dependencies(
    val router: Router<String> = routerOf("connection"),
    override val serialization: Serialization = Serialization(),
    override val terminalErrorHandler: TerminalErrorHandler = TerminalErrorHandler(),
    val state: State = State(),
) : CommonDependencies {
    val userInterface: UserInterface = UserInterface(this)
    val uiErrorHandler: UIErrorHandler = UIErrorHandler(userInterface, terminalErrorHandler)
    val eventHandler: EventHandler = EventHandler(this, userInterface, state, uiErrorHandler)

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

    inner class ConnectionScope(
        override val connection: Connection,
        override val serialization: Serialization = this@Dependencies.serialization,
        override val terminalErrorHandler: TerminalErrorHandler = this@Dependencies.terminalErrorHandler,
        override val connectionCommunicator: ConnectionCommunicator =
            ConnectionCommunicator(connection, serialization),
        override val connectionErrorHandler: ConnectionErrorHandler = ConnectionErrorHandler(
            terminalErrorHandler,
            connectionCommunicator
        ),
        val userInterface: UserInterface = this@Dependencies.userInterface,
        val eventHandler: EventHandler = this@Dependencies.eventHandler,
        val state: State = this@Dependencies.state,
        val uiErrorHandler: UIErrorHandler = this@Dependencies.uiErrorHandler
    ) : CommonDependencies.ConnectionScope {



        init {
            state.connectionScope.value = this
        }
    }
}



