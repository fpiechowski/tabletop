package tabletop.client.connection

import arrow.core.raise.catch
import arrow.core.raise.either
import io.ktor.websocket.*
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError

suspend fun Connection.logout() = either {
    catch({ session.close(CloseReason(CloseReason.Codes.GOING_AWAY, "logout")) })
    { raise(Connection.Error("Error when logging out", CommonError.ThrowableError(it))) }
}


