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
) : CommonDependencies {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    override val persistence: Persistence by lazyPersistence
    private val connectionScopeFactory get() = { connection: Connection -> ConnectionScope(connection) }
    val serverAdapter: ServerAdapter by lazy { ServerAdapter(connectionScopeFactory) }
    val demo: Demo = Demo(persistence)
    val state: State by lazy { runBlocking { State() } }

    inner class ConnectionScope(
        override val connection: Connection,
        val persistence: Persistence = this@Dependencies.persistence,
        val state: State = this@Dependencies.state,
        override val serialization: Serialization = this@Dependencies.serialization,
        override val terminalErrorHandler: TerminalErrorHandler = this@Dependencies.terminalErrorHandler,
        override val connectionCommunicator: ConnectionCommunicator =
            ConnectionCommunicator(connection, serialization),
        override val connectionErrorHandler: ConnectionErrorHandler =
            ConnectionErrorHandler(terminalErrorHandler, connectionCommunicator)
    ) : CommonDependencies.ConnectionScope {

        val authentication: Authentication = AuthenticationAdapter(this@ConnectionScope)
        val eventHandler: EventHandler = EventHandler(this)
    }

    fun interface ConnectionScopeFactory {
        operator fun invoke(connection: Connection): ConnectionScope
    }
}


