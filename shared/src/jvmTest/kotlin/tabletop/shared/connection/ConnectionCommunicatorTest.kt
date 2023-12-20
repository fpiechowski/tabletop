package tabletop.shared.connection

import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.websocket.*
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import tabletop.shared.serialization.Serialization
import kotlin.test.Test
import kotlin.test.fail

class ConnectionCommunicatorTest {

    private val session = mockk<WebSocketSession> {
        coEvery { send(any()) } returns Unit
    }
    private val serialization = Serialization()
    private val connectionCommunicator = ConnectionCommunicator(
        Connection(
            "localhost",
            8080,
            session
        ),
        serialization
    )

    @Serializable
    data class TestMessage(val test: String)

    @Test
    fun send() = runTest {
        val result = with(connectionCommunicator) {
            TestMessage("test").send()
        }

        result.shouldBeRight()

        val frame = slot<Frame.Text>()

        coVerify {
            session.send(capture(frame))
        }

        frame.captured.readText() shouldEqualJson """
            {
                "test": "test"
            }
        """.trimIndent()
    }

    @Test
    fun receive() = runTest {
        val incomingChannel = Channel<Frame>(1)
        every { session.incoming } returns incomingChannel

        launch { incomingChannel.send(Frame.Text("""{"test": "test"}""")) }

        val received = CompletableDeferred<TestMessage>()

        backgroundScope.launch {
            with(connectionCommunicator) {
                receiveIncoming<TestMessage>(
                    onEach = {
                        received.complete(it)
                    },
                    onError = { fail(it.toString()) }
                )
            }
        }

        received.await().shouldBeEqual(TestMessage("test"))
    }
}