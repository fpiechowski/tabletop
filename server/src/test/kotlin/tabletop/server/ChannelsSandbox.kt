package tabletop.server

import io.kotest.core.spec.style.FreeSpec
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ChannelsSandbox : FreeSpec({
    "channels" {
        val channel = Channel<String>()

        launch {
            channel.send("test 1")
        }

        delay(1000)

        launch {
            channel.receiveAsFlow().collect {
                println("received $it")
            }
        }

        channel.send("test2")

        channel.close()
    }
})