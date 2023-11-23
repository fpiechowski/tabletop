package tabletop.server.di

import kotlinx.coroutines.Job
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
import tabletop.server.persistence.Persistence.Root
import tabletop.server.state.State

class Dependencies(
    lazyPersistence: Lazy<Persistence> = lazy {
        Persistence(EmbeddedStorage.start(Root()))
    }
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
    val serverJob: Job = Job()
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

