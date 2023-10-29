package tabletop.client

import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.sceneContainer
import korlibs.math.geom.Size
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tabletop.client.connection.ConnectionScene
import tabletop.client.di.DependenciesAdapter

fun main() = runBlocking {
    val dependencies = DependenciesAdapter()
    runKorge(dependencies)
}

private suspend fun runKorge(dependencies: DependenciesAdapter) =
    with(dependencies) {
        Korge(
            virtualSize = Size(1280, 720),
            windowSize = Size(1280, 720),
            backgroundColor = Colors["#2b2b2b"]
        ) {
            val sceneContainer = sceneContainer()

            dependencies.userInterface.stage.complete(this)

            injector.mapInstance(dependencies)

            launch {
                sceneContainer.changeTo { ConnectionScene() }
            }
        }
    }