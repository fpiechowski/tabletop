package tabletop.client

import dev.fritz2.core.render
import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import tabletop.client.connection.connectionScene
import tabletop.client.di.Dependencies
import tabletop.client.game.gameScreen
import tabletop.client.ui.importantFrame

fun main() {
    val dependencies = Dependencies().also { Dependencies.instance.complete(it) }
    KotlinLoggingConfiguration.logLevel = Level.DEBUG

    render {
        dependencies.userInterface.notifications.run { render() }

        dependencies.router.data.render {
            when (it) {
                "connection" -> connectionScene(dependencies)
                "game" -> gameScreen(dependencies)
                "important" -> div("w-1/4 p-10") { importantFrame { +"LAlala" } }
            }
        }
    }
}