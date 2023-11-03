package tabletop.client.game

import arrow.fx.stm.atomically
import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.draggableCloseable
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.ui.UIButton
import korlibs.korge.ui.uiButton
import korlibs.korge.ui.uiScrollable
import korlibs.korge.ui.uiWindow
import korlibs.korge.view.*
import korlibs.korge.view.align.alignTopToBottomOf
import korlibs.korge.view.align.centerOnStage
import korlibs.math.geom.Vector2
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.SceneOpened
import tabletop.client.ui.uiScaling
import korlibs.korge.scene.Scene as UIScene


@KorgeExperimental
@KorgeInternal
class GameScene : UIScene() {
    private suspend fun game() = Dependencies.await().state.game.let { atomically { it.read() } }

    override suspend fun SContainer.sceneMain() {
        scene()
        container {
            libraryButton()
        }
    }

    private fun SContainer.scene() {
        fun SContainer.sceneMouseControl(contentContainer: View) {
            var rightClick = false

            mouse.onDownCloseable {
                if (it.button.isRight) {
                    rightClick = true
                }
            }
            mouse.onUpCloseable {
                if (it.button.isRight) {
                    rightClick = false
                }
            }
            draggableCloseable(autoMove = false) {
                val dragging = rightClick

                if (dragging) {
                    val dragDeltaX = it.deltaDx
                    val dragDeltaY = it.deltaDy

                    contentContainer.pos -= Vector2(dragDeltaX, dragDeltaY)
                }
            }
            mouse.scroll { event ->
                val zoomFactor = 1.1f
                val scale = if (event.scrollDeltaYPixels < 0) 1 / zoomFactor else zoomFactor

                // Calculate the position to zoom in/out on
                val mouseXY = event.currentPosGlobal
                val containerXY = contentContainer.pos

                contentContainer.scaleX *= scale
                contentContainer.scaleY *= scale

                contentContainer.x = mouseXY.x - (mouseXY.x - containerXY.x) * scale
                contentContainer.y = mouseXY.y - (mouseXY.y - containerXY.y) * scale
            }
        }

        sceneView.container {
            onEvent(SceneOpened) {
                removeChildren()

                launch {
                    val contentContainer = container {
                        it.scene.foregroundImagePath?.let {
                            image(applicationVfs[it].readBitmap())
                        }
                    }

                    this@scene.sceneMouseControl(contentContainer)
                }
            }
        }
    }

    private fun libraryButton() {
        sceneView.uiButton("Library") {
            onClick {
                libraryWindow()
            }

            width = 100f
            height = 30f

            uiScaling()
        }.xy(20, 20)
    }

    private suspend fun libraryWindow() {
        sceneView.uiWindow("Library") {
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