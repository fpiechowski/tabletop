package tabletop.client.di

import kotlinx.coroutines.CompletableDeferred
import tabletop.client.assets.AssetStorage
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.client.persistence.Persistence
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
    override val persistence: Persistence = Persistence(),
    override val serialization: Serialization = Serialization(),
    override val terminalErrorHandler: TerminalErrorHandler = TerminalErrorHandler(),
    val state: State = State(),
    val userInterface: UserInterface = UserInterface(state),
    val uiErrorHandler: UIErrorHandler = UIErrorHandler(userInterface, terminalErrorHandler)
) : CommonDependencies {
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
        val assetStorage: AssetStorage = AssetStorage(connection),
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



