package tabletop.client.di

import arrow.fx.stm.TMVar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import tabletop.client.command.CommandResultExecutor
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.Event
import tabletop.client.event.EventHandler
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.command.Command
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization

class DependenciesAdapter(val mainCoroutineScope: CoroutineScope) : CommonDependencies() {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    override val commandChannel: Command.Channel by lazy { Command.Channel() }
    override val commandResultChannel: Command.Result.Channel by lazy { Command.Result.Channel() }
    val commandResultExecutor: CommandResultExecutor by lazy { CommandResultExecutor(this) }
    val uiErrorHandler: UIErrorHandler by lazy { UIErrorHandler(this) }
    val userInterface: UserInterface by lazy { UserInterface() }
    val eventHandler: EventHandler by lazy { EventHandler(this) }
    val eventChannel: Event.Channel by lazy { Event.Channel() }
    val state: State by lazy { runBlocking { State(TMVar.empty(), TMVar.empty(), TMVar.empty()) } }

    inner class ConnectionScope(override val connection: Connection) : CommonDependencies.ConnectionScope() {
        override val connectionCommunicator: ConnectionCommunicator by lazy { ConnectionCommunicator(this) }
        override val connectionErrorHandler: ConnectionErrorHandler by lazy { ConnectionErrorHandler(this) }
    }
}



