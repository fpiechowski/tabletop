package tabletop.client

import arrow.fx.stm.atomically
import io.github.oshai.kotlinlogging.KotlinLogging
import io.mockk.every
import io.mockk.mockk
import korlibs.event.ReshapeEvent
import korlibs.image.color.Colors
import korlibs.korge.Korge
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.scene.Scene
import korlibs.korge.scene.sceneContainer
import korlibs.korge.view.SContainer
import korlibs.math.geom.Anchor
import korlibs.math.geom.ScaleMode
import korlibs.math.geom.Size
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tabletop.client.di.Dependencies
import tabletop.client.persistence.Persistence
import tabletop.client.settings.Settings
import tabletop.common.demo.demoGame

private val logger = KotlinLogging.logger { }

@KorgeInternal
@KorgeExperimental
fun main() = runBlocking {
    runKorge()
}

@KorgeExperimental
@KorgeInternal
private suspend fun runKorge() =
    Settings().run {
        Korge(
            virtualSize = virtualSize,
            windowSize = windowSize,
            backgroundColor = Colors["#2b2b2b"],
            scaleAnchor = Anchor.MIDDLE_CENTER,
            scaleMode = ScaleMode.SHOW_ALL,
            clipBorders = false,
        ) {
            val sceneContainer = sceneContainer()

            onEvent(ReshapeEvent) {
                logger.debug { "in onReshapeEvent $it" }
                this.size = Size(it.width, it.height)
            }

            launch {
                sceneContainer.changeTo { OfflineDependenciesScene }
            }
        }
    }

@KorgeInternal
@KorgeExperimental
object OfflineDependenciesScene : Scene() {

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