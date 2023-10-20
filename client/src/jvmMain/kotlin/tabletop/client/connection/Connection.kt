package tabletop.client.connection

import arrow.core.raise.Raise
import arrow.core.raise.catch
import io.ktor.websocket.*
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError

context (Raise<Connection.Error>)
suspend fun Connection.logout() = catch({ session.close(CloseReason(CloseReason.Codes.GOING_AWAY, "logout")) })
{ raise(Connection.Error("Error when logging out", CommonError.ThrowableError(it))) }

class ClientConnectionAdapter(val connection: Connection)

