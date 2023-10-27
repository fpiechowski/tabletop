package tabletop.client

import arrow.continuations.SuspendApp
import arrow.core.raise.either
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import tabletop.client.di.DependenciesAdapter
import tabletop.client.ui.UserInterface

fun main() = SuspendApp {
    val dependencies = DependenciesAdapter(this)

    with(dependencies) {
        eventChannel.receiveAsFlow {
            with(eventHandler) { either { it.handle().bind() } }
        }

        userInterface.runKorge()
    }
}

private suspend fun UserInterface.runKorge() = Korge {
    val sceneContainer = sceneContainer()

    // TODO sceneContainer.changeTo { MyScene() }
}
