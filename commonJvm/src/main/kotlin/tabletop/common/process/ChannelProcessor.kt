package tabletop.common.process

import arrow.core.raise.Raise
import arrow.core.raise.catch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import tabletop.common.error.CommonError
import tabletop.common.logging.logger

abstract class ChannelProcessor<T>(val channel: Channel<T> = Channel()) {

    companion object

    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}


context (Raise<ChannelProcessor.Error>, ChannelProcessor<T>)
suspend inline fun <reified T> T.publish() = catch({
    channel.send(this)
    ChannelProcessor.logger.debug { "Published $this" }
}) {
    raise(ChannelProcessor.Error("Error when publishing $this", CommonError.ThrowableError(it)))
}

context (ChannelProcessor<T>)
suspend inline fun <reified T> startProcessing(crossinline onCollected: suspend (T) -> Unit) {
    ChannelProcessor.logger.debug { "Launching processing of ${T::class.qualifiedName}s" }
    channel.receiveAsFlow()
        .collect { command ->
            ChannelProcessor.logger.debug { "Collected $command" }
            onCollected(command)
        }
}