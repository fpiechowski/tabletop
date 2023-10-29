package tabletop.client.di

import arrow.fx.stm.TMVar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import tabletop.client.command.CommandResultExecutor
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization

class DependenciesAdapter : CommonDependencies() {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    val commandResultExecutor: CommandResultExecutor by lazy { CommandResultExecutor(this) }
    val uiErrorHandler: UIErrorHandler by lazy { UIErrorHandler(this) }
    val userInterface: UserInterface by lazy { UserInterface() }
    val eventHandler: EventHandler by lazy { EventHandler(this) }
    val state: State by lazy { runBlocking { State(TMVar.empty(), TMVar.empty(), TMVar.empty()) } }

    inner class ConnectionScope(override val connection: Connection) : CommonDependencies.ConnectionScope(),
        CoroutineScope by CoroutineScope(Dispatchers.Default) {
        override val connectionCommunicator: ConnectionCommunicator by lazy { ConnectionCommunicator(this) }
        override val connectionErrorHandler: ConnectionErrorHandler by lazy { ConnectionErrorHandler(this) }
    }
}



