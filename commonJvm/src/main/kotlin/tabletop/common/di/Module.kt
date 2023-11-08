package tabletop.common.di

import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.persistence.Persistence
import tabletop.common.serialization.Serialization

abstract class CommonDependencies {
    abstract val persistence: Persistence<*>
    abstract val serialization: Serialization
    abstract val terminalErrorHandler: TerminalErrorHandler

    abstract inner class ConnectionScope {
        abstract val connection: Connection
        abstract val ConnectionCommunicator.Aware.connectionCommunicator: ConnectionCommunicator
        abstract val connectionErrorHandler: ConnectionErrorHandler
        val serialization: Serialization = this@CommonDependencies.serialization
        val terminalErrorHandler: TerminalErrorHandler = this@CommonDependencies.terminalErrorHandler
    }
}

