package tabletop.client.game

import arrow.fx.stm.atomically
import io.github.oshai.kotlinlogging.KotlinLogging
import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.input.draggableCloseable
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.ui.UIButton
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiScrollable
import korlibs.korge.ui.uiWindow
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.align.centerOnStage
import korlibs.korge.view.container
import korlibs.korge.view.image
import korlibs.korge.view.xy
import korlibs.math.geom.Vector2
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.SceneOpened
import korlibs.korge.scene.Scene as UIScene


class GameScene : UIScene() {
    private val logger = KotlinLogging.logger { }
    private suspend fun game() = Dependencies.await().state.game.let { atomically { it.read() } }

    override suspend fun SContainer.sceneMain() {
        scene()

        libraryButton()
    }

    private fun SContainer.scene() {
        sceneView.container {
            onEvent(SceneOpened) {
                removeChildren()

                launch {
                    val contentContainer = container {
                        it.scene.foregroundImagePath?.let {
                            image(applicationVfs[it].readBitmap())
                        }
                    }

                    var rightClick = false

                    this@scene.mouse.onDownCloseable {
                        if (it.button.isRight) {
                            rightClick = true
                        }
                    }
                    this@scene.mouse.onUpCloseable {
                        if (it.button.isRight) {
                            rightClick = false
                        }
                    }
                    this@scene.draggableCloseable(autoMove = false) {
                        logger.debug { "${it.mouseEvents.buttons}" }
                        val dragging = rightClick
                        logger.debug { "dragging set $dragging" }

                        if (dragging) {
                            logger.debug { "dragging" }
                            val dragDeltaX = it.deltaDx
                            val dragDeltaY = it.deltaDy

                            contentContainer.pos -= Vector2(dragDeltaX, dragDeltaY)
                        }
                    }
                    this@scene.mouse.scroll { event ->
                        val zoomFactor = 1.1f
                        val scale = if (event.scrollDeltaY < 0) 1 / zoomFactor else zoomFactor

                        // Calculate the position to zoom in/out on
                        val mouseXY = event.currentPosGlobal
                        val containerXY = contentContainer.pos

                        contentContainer.scaleX *= scale
                        contentContainer.scaleY *= scale

                        contentContainer.x = mouseXY.x - (mouseXY.x - containerXY.x) * scale
                        contentContainer.y = mouseXY.y - (mouseXY.y - containerXY.y) * scale
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
                            with(Dependencies.await().eventHandler) {
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