package tabletop.server.di

import kotlinx.coroutines.runBlocking
import one.microstream.storage.embedded.types.EmbeddedStorage
import tabletop.common.auth.Authentication
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization
import tabletop.server.ServerAdapter
import tabletop.server.auth.AuthenticationAdapter
import tabletop.server.demo.Demo
import tabletop.server.event.EventHandler
import tabletop.server.persistence.Persistence
import tabletop.server.state.State

class Dependencies(
    lazyPersistence: Lazy<Persistence> = lazy {
        Persistence(EmbeddedStorage.start(Persistence.Root()))
    }
) : CommonDependencies() {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    override val persistence: Persistence by lazyPersistence
    val serverAdapter: ServerAdapter by lazy { ServerAdapter(this) }
    val demo: Demo = Demo(persistence)
    val state: State by lazy { runBlocking { State() } }

    inner class ConnectionScope(
        override val connection: Connection,
    ) : CommonDependencies.ConnectionScope() {

        val persistence: Persistence = this@Dependencies.persistence

        val authentication: Authentication by lazy { AuthenticationAdapter(this) }
        val eventHandler: EventHandler by lazy { EventHandler(this) }
        val state: State by lazy { this@Dependencies.state }

        override val ConnectionCommunicator.Aware.connectionCommunicator: ConnectionCommunicator by lazy {
            ConnectionCommunicator(this)
        }
        override val connectionErrorHandler: ConnectionErrorHandler by lazy {
            ConnectionErrorHandler(this)
        }
    }
}


