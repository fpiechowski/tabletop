package tabletop.client.di

import io.ktor.websocket.*
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.client.persistence.Persistence
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization

@KorgeExperimental
@KorgeInternal
open class Dependencies(
    persistence: Lazy<Persistence> = lazy { Persistence() },
) : CommonDependencies() {

    companion object : CompletableDeferred<Dependencies> by CompletableDeferred();

    override val persistence: Persistence by persistence
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    val uiErrorHandler: UIErrorHandler by lazy { UIErrorHandler(this) }
    val userInterface: UserInterface by lazy { UserInterface() }
    val eventHandler: EventHandler by lazy { EventHandler(this) }
    val state: State by lazy { State() }

    val connectionScope: MutableStateFlow<ConnectionScope?> = MutableStateFlow(null)

    inner class ConnectionScope(override val connection: Connection) : CommonDependencies.ConnectionScope(),
        CoroutineScope by CoroutineScope(Dispatchers.Default) {

        init {
            runBlocking {
                connectionScope.value?.connection?.session?.close(
                    CloseReason(
                        CloseReason.Codes.GOING_AWAY,
                        "Client attempt another connection"
                    )
                )
                connectionScope.value = this@ConnectionScope
            }
        }

        override val ConnectionCommunicator.Aware.connectionCommunicator: ConnectionCommunicator by lazy {
            ConnectionCommunicator(
                this
            )
        }
        override val connectionErrorHandler: ConnectionErrorHandler by lazy { ConnectionErrorHandler(this) }

        val eventHandler: EventHandler by lazy { this@Dependencies.eventHandler }
        val uiErrorHandler: UIErrorHandler by lazy { this@Dependencies.uiErrorHandler }
        val state: State by lazy { this@Dependencies.state }
        val userInterface: UserInterface by lazy { this@Dependencies.userInterface }
    }
}



