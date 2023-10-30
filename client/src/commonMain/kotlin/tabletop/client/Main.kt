package tabletop.client

import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Size
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
            virtualSize = Size(1280, 720),
            windowSize = Size(1280, 720),
            backgroundColor = Colors["#2b2b2b"]
        ) {
            val sceneContainer = sceneContainer()

            dependencies.userInterface.stage.complete(this)
            dependencies.userInterface.sceneContainer.complete(sceneContainer)

            injector.mapInstance(dependencies)

            launch {
                sceneContainer.changeTo { userInterface.connectionScene }
            }
        }
    }