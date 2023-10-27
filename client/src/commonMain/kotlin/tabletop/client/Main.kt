package tabletop.client

import arrow.continuations.SuspendApp
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import tabletop.client.di.DependenciesAdapter
import tabletop.client.ui.UserInterface

fun main() = SuspendApp {
    val dependenciesAdapter = DependenciesAdapter(this)
    dependenciesAdapter.userInterface.runKorge()
}

private suspend fun UserInterface.runKorge() = Korge {
    val sceneContainer = sceneContainer()

    // TODO sceneContainer.changeTo { MyScene() }
}
