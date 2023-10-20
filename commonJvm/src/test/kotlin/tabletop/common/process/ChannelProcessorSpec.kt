package tabletop.common.process

import arrow.core.raise.either
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.mockk.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tabletop.common.error.CommonError
import kotlin.time.Duration.Companion.seconds

typealias StartProcessing<T> = suspend (ChannelProcessor<T>, suspend (T) -> Unit) -> Unit

class ChannelProcessorSpec : FreeSpec({

    class TestData

    val channel: Channel<TestData> = mockk()
    mockkStatic("kotlinx.coroutines.flow.FlowKt")
    val channelProcessor = object : ChannelProcessor<TestData>(channel) {}
    val testData = TestData()

    with(channelProcessor) {
        "${ChannelProcessor<TestData>::publish}" - {
            "with no exceptions" - {
                coEvery { channel.send(any()) } returns Unit
                val publishResult = either { testData.publish() }

                "sends to channel" {
                    coVerify { channel.send(testData) }
                }

                "result is right" {
                    publishResult.shouldBeRight()
                }
            }

            "with exceptions" - {
                val exception = Exception()

                coEvery { channel.send(any()) } throws exception

                val publishResult = either {
                    testData.publish()
                }

                "result is left" {
                    publishResult.shouldBeLeft()
                        .shouldBeEqualToComparingFields(
                            ChannelProcessor.Error(
                                "Error when publishing $testData",
                                CommonError.ThrowableError(exception)
                            )
                        )
                }
            }
        }

        "testData in channel" - {
            coEvery { channel.receiveAsFlow() } returns flowOf(testData)

            "startProcessing" - {
                val process = mockk<(TestData) -> Unit>()
                every { process(any()) } returns Unit

                launch {
                    startProcessing<TestData> {
                        process.invoke(it)
                    }
                }

                "eventually invoking process function" {
                    eventually(2.seconds) {
                        verify { process(testData) }
                    }
                }
            }
        }
    }
})