package tabletop.client

import arrow.fx.stm.atomically
import io.github.oshai.kotlinlogging.KotlinLogging
import io.mockk.every
import io.mockk.mockk
import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.SContainer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tabletop.client.di.Dependencies
import tabletop.client.persistence.Persistence
import tabletop.client.settings.Settings
import tabletop.common.demo.demoGame


fun main() = runBlocking {
    runKorge()
}

private suspend fun runKorge() =
    Settings().run {
        Korge(
            virtualSize = virtualSize,
            windowSize = windowSize,
            backgroundColor = Colors["#2b2b2b"]
        ) {
            val sceneContainer = sceneContainer()

            launch {
                sceneContainer.changeTo { OfflineDependenciesScene }
            }
        }
    }

object OfflineDependenciesScene : Scene() {
    private val logger = KotlinLogging.logger { }

    override suspend fun SContainer.sceneMain() {
        Dependencies.run {
            complete(
                Dependencies(lazy {
                    val root = Persistence.Root()
                    Persistence(root, mockk(relaxed = true) { every { root() } returns root as Any? })
                })
            )

            await().apply {
                stage?.let { userInterface.stage.complete(it) }
                userInterface.sceneContainer.complete(sceneContainer)
                atomically { state.game.tryPut(demoGame) }

                sceneContainer.changeTo { userInterface.gameScene }
            }
        }

    }
}