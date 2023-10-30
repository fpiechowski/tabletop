package tabletop.client.game

import arrow.fx.stm.atomically
import korlibs.image.format.readBitmap
import korlibs.io.file.std.resourcesVfs
import korlibs.korge.input.onClick
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.align.centerOnStage
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.EventHandler
import tabletop.client.event.SceneOpened
import tabletop.client.settings.Settings
import java.util.concurrent.CompletableFuture
import korlibs.korge.scene.Scene as UIScene

class GameScene : UIScene() {

    private val dependencies = CompletableFuture<Dependencies>()
    private val eventHandler = CompletableFuture<EventHandler>()
    private val settings = CompletableFuture<Settings>()
    private suspend fun game() = dependencies.await().state.game.let { atomically { it.read() } }

    override suspend fun SContainer.sceneMain() {
        injector.get<Dependencies>()
            .also { dependencies.complete(it) }
            .also {
                eventHandler.complete(it.eventHandler)
                settings.complete(it.persistence.persistenceRoot.settings)
            }

        scene()

        libraryButton()

        uiText("GameScene").centerOnStage()
    }

    private fun scene() {
        sceneView.container {
            onEvent(SceneOpened) {
                removeChildren()

                launch {
                    clipContainer(settings.await().virtualSize) {
                        camera {
                            image(resourcesVfs["demo/sceneImage.png"].readBitmap())
                        }
                    }
                }
            }
        }
    }

    private fun libraryButton() {
        sceneView.uiButton("Library") {
            onClick {
                libraryWindow()
            }
        }.xy(20, 20)
    }

    private suspend fun libraryWindow() {
        sceneView.uiWindow("Library") { window ->
            uiScrollable {
                val scenes = game().scenes.values

                scenes.map { scene ->
                    uiButton(scene.name) {
                        onClick {
                            with(eventHandler.await()) {
                                SceneOpened(scene).handle()
                            }
                        }
                    }
                }.fold(listOf<UIButton>()) { prevList, next ->
                    next.alignTopToBottomOf(prevList.last(), 10)
                    prevList + next
                }
            }
        }.centerOnStage()
    }
}