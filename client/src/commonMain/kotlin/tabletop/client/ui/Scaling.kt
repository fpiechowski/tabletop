package tabletop.client.ui

import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.event.ReshapeEvent
import korlibs.korge.view.View
import korlibs.korge.view.scale

private val logger = KotlinLogging.logger { }

fun <T : View> T.uiScaling() {
    onEvent(ReshapeEvent) { event ->
        val scaleFactor = (this.stage?.let { it.views.virtualWidth.toFloat() / event.width.toFloat() } ?: 1f)
        scale(scaleFactor, scaleFactor)
        logger.debug { "uiScaling(x$scaleFactor) $this" }
    }
}

