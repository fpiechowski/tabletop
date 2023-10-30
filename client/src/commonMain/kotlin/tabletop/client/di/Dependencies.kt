package tabletop.client.di

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically
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

class Dependencies : CommonDependencies() {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    val uiErrorHandler: UIErrorHandler by lazy { UIErrorHandler(this) }
    val userInterface: UserInterface by lazy { UserInterface() }
    val eventHandler: EventHandler by lazy { EventHandler(this) }
    val state: State by lazy { runBlocking { State(TMVar.empty(), TMVar.empty(), TMVar.empty()) } }
    private val connectionScope: TMVar<ConnectionScope> = runBlocking { TMVar.empty() }
    suspend fun connectionScope() = atomically { connectionScope.tryRead() }

    inner class ConnectionScope(override val connection: Connection) : CommonDependencies.ConnectionScope(),
        CoroutineScope by CoroutineScope(Dispatchers.Default) {

        init {
            runBlocking {
                atomically {
                    if (!connectionScope.tryPut(this@ConnectionScope))
                        connectionScope.swap(this@ConnectionScope)
                }
            }
        }

        override val connectionCommunicator: ConnectionCommunicator by lazy { ConnectionCommunicator(this) }
        override val connectionErrorHandler: ConnectionErrorHandler by lazy { ConnectionErrorHandler(this) }
        val commandResultExecutor: CommandResultExecutor by lazy { CommandResultExecutor(this) }
        val eventHandler: EventHandler by lazy { this@Dependencies.eventHandler }
        val uiErrorHandler: UIErrorHandler by lazy { this@Dependencies.uiErrorHandler }
        val state: State by lazy { this@Dependencies.state }
        val userInterface: UserInterface by lazy { this@Dependencies.userInterface }
    }
}



