package tabletop.client.game

import arrow.fx.stm.atomically
import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.input.onClick
import korlibs.korge.input.onScroll
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.align.centerOnStage
import korlibs.memory.clamp
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.EventHandler
import tabletop.client.event.SceneOpened
import tabletop.client.settings.Settings
import java.util.concurrent.CompletableFuture
import korlibs.korge.scene.Scene as UIScene

class GameScene : UIScene() {
    private val logger = KotlinLogging.logger { }
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
                            val foregroundImage = it.scene.foregroundImagePath?.let {
                                image(applicationVfs[it].readBitmap())
                            }

                            var zoom = 1f

                            onScroll {
                                zoom += it.scrollDeltaYPixels * 0.01f
                                zoom = zoom.clamp(0.1f, 5f)
                                foregroundImage?.scale(zoom, zoom)
                            }
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
                    prevList.lastOrNull()?.let { next.alignTopToBottomOf(it, 10) }
                    prevList + next
                }
            }
        }.centerOnStage()
    }
}