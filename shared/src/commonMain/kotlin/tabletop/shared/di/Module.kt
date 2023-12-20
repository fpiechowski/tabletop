package tabletop.shared.di

import tabletop.shared.connection.Connection
import tabletop.shared.connection.ConnectionCommunicator
import tabletop.shared.error.ConnectionErrorHandler
import tabletop.shared.error.TerminalErrorHandler
import tabletop.shared.serialization.Serialization

interface CommonDependencies {
    val serialization: Serialization
    val terminalErrorHandler: TerminalErrorHandler

    interface ConnectionScope {
        val connection: Connection
        val connectionCommunicator: ConnectionCommunicator
        val connectionErrorHandler: ConnectionErrorHandler
    }
}

