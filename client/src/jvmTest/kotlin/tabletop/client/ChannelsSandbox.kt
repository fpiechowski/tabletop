package tabletop.client

import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.time.Duration.Companion.seconds

class ChannelsSandbox : FreeSpec({
    "channels" {
        val channel = Channel<String>()

        repeat(3) {
            channel.send("first")
        }

        channel.receiveAsFlow()
            .collect {
                println(it)
            }

        delay(3.seconds)

        repeat(3) {
            channel.send("second")
        }

        channel.receiveAsFlow()
            .collect {
                println(it)
            }
    }
})