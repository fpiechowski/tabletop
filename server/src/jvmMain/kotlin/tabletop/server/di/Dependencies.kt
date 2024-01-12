package tabletop.server.di

import kotlinx.coroutines.runBlocking
import tabletop.server.ServerAdapter
import tabletop.server.auth.AuthenticationAdapter
import tabletop.server.demo.Demo
import tabletop.server.event.EventHandler
import tabletop.server.persistence.Persistence
import tabletop.server.state.State
import tabletop.shared.connection.Connection
import tabletop.shared.connection.ConnectionCommunicator
import tabletop.shared.di.CommonDependencies
import tabletop.shared.error.ConnectionErrorHandler
import tabletop.shared.error.TerminalErrorHandler
import tabletop.shared.serialization.Serialization


class Dependencies(
    lazyPersistence: Lazy<Persistence> = lazy { Persistence() }
) : CommonDependencies {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    val persistence: Persistence by lazyPersistence
    val connectionDependenciesFactory: ConnectionDependencies.Factory =
        ConnectionDependencies.Factory { connection: Connection ->
            ConnectionDependencies(
                this,
                connection
            )
        }
    val authentication = AuthenticationAdapter(this)
    val serverAdapter: ServerAdapter by lazy { ServerAdapter(this) }
    val demo: Demo = Demo(persistence)
    val state: State by lazy { runBlocking { State() } }
}

class ConnectionDependencies(
    dependencies: Dependencies,
    override val connection: Connection,
    override val connectionCommunicator: ConnectionCommunicator =
        ConnectionCommunicator(connection, dependencies.serialization),
    override val connectionErrorHandler: ConnectionErrorHandler =
        ConnectionErrorHandler(dependencies.terminalErrorHandler, connectionCommunicator)
) : CommonDependencies.ConnectionScope {

    val eventHandler: EventHandler = EventHandler(dependencies, this)

    fun interface Factory : (Connection) -> ConnectionDependencies {
        override operator fun invoke(connection: Connection): ConnectionDependencies
    }
}

