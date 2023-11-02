package tabletop.client

import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tabletop.client.di.Dependencies

fun main() = runBlocking {
    val dependencies = Dependencies()
    runKorge(dependencies)
}

private suspend fun runKorge(dependencies: Dependencies) =
    with(dependencies) {
        Korge(
            virtualSize = dependencies.persistence.persistenceRoot.settings.virtualSize,
            windowSize = dependencies.persistence.persistenceRoot.settings.windowSize,
            backgroundColor = Colors["#2b2b2b"]
        ) {
            val sceneContainer = sceneContainer()

            dependencies.userInterface.stage.complete(this)
            dependencies.userInterface.sceneContainer.complete(sceneContainer)

            launch {
                sceneContainer.changeTo { userInterface.connectionScene }
            }
        }
    }
