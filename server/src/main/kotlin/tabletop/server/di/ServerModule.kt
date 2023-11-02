package tabletop.server.di

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
import tabletop.server.command.CommandExecutor
import tabletop.server.demo.Demo
import tabletop.server.persistence.Persistence

class DependenciesAdapter(
    lazyPersistence: Lazy<Persistence> = lazy {
        Persistence(EmbeddedStorage.start(Persistence.Root()))
    }
) : CommonDependencies() {
    override val serialization: Serialization by lazy { Serialization() }
    override val terminalErrorHandler: TerminalErrorHandler by lazy { TerminalErrorHandler() }
    override val persistence: Persistence by lazyPersistence
    val serverAdapter: ServerAdapter by lazy { ServerAdapter(this) }
    val demo: Demo = Demo(persistence)

    inner class ConnectionScope(
        override val connection: Connection,
    ) : CommonDependencies.ConnectionScope() {

        val persistence: Persistence = this@DependenciesAdapter.persistence

        val authentication: Authentication by lazy { AuthenticationAdapter(this) }
        val commandExecutor: CommandExecutor by lazy { CommandExecutor(this) }

        override val connectionCommunicator: ConnectionCommunicator by lazy {
            ConnectionCommunicator(this)
        }
        override val connectionErrorHandler: ConnectionErrorHandler by lazy {
            ConnectionErrorHandler(this)
        }
    }
}


