package tabletop.common.process

import arrow.core.raise.catch
import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import tabletop.common.error.CommonError

abstract class CommonChannel<T>(
    val channel: Channel<T> = Channel()
) {
    abstract val logger: KLogger
    suspend inline fun T.publish() = either {
        catch({
            channel.send(this@publish)
            logger.debug { "Published ${this@publish}" }
        }) {
            raise(Error("Error when publishing ${this@publish}", CommonError.ThrowableError(it)))
        }
    }

    suspend inline fun receiveAsFlow(crossinline onCollected: suspend (T) -> Unit) {
        channel.receiveAsFlow()
            .collect {
                logger.debug { "Collected $it`" }
                onCollected(it)
            }
    }

    companion object

    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}