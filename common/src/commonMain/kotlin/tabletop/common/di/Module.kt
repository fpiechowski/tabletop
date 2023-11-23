package tabletop.common.di

import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization

interface CommonDependencies {
    val serialization: Serialization
    val terminalErrorHandler: TerminalErrorHandler

    interface ConnectionScope {
        val connection: Connection
        val connectionCommunicator: ConnectionCommunicator
        val connectionErrorHandler: ConnectionErrorHandler
    }
}

