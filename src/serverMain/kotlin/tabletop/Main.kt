package tabletop

import arrow.continuations.SuspendApp
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.*
import java.util.concurrent.atomic.AtomicInteger

fun main() = SuspendApp {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        commandHandler()
    }.start(wait = true)
}

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}

data class ExceptionTextFrame(
    val error: String,
    val message: String?,
    val cause: String?
) {
    companion object {
        fun <T : CommonError> from(exception: T) = ExceptionTextFrame(
            error = exception::class.simpleName!!,
            message = exception.message,
            cause = exception.cause!!::class.simpleName
        )
    }
}
