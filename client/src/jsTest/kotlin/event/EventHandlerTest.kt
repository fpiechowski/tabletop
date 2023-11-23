package event

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.statistics.outputStatistics
import tabletop.client.di.Dependencies
import tabletop.client.event.ConnectionAttempted
import tabletop.common.auth.Credentials
import tabletop.common.event.Event

class EventHandlerTest : FreeSpec({

    val dependencies = Dependencies()

    "EventHandler" - {
        val eventHandler = dependencies.eventHandler

        "handle" - {
            val handle: suspend Event.() -> Unit = {
                with(eventHandler) {
                    handle()
                }
            }

            "${ConnectionAttempted::class}" {
                val event = ConnectionAttempted("host", 0, Credentials.UsernamePassword.Data("username", "password"))
                val previousConnectionJob = dependencies.state.connectionJob.value.shouldNotBeNull()

                event.handle()

                previousConnectionJob.isCancelled.shouldBeTrue()
                dependencies.state.connectionJob.value.shouldNotBe(previousConnectionJob)
            }
        }
    }
})
