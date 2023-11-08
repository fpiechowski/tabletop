package tabletop.client.scene

import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.draggableCloseable
import korlibs.korge.input.mouse
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.view.View
import korlibs.korge.view.container
import korlibs.korge.view.image
import korlibs.math.geom.Vector2
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.SceneOpenedUIEvent
import tabletop.client.event.TokenPlacedUIEvent
import tabletop.client.game.GameScene
import tabletop.client.scene.token.tokenView

@KorgeInternal
@KorgeExperimental
suspend fun currentScene() = Dependencies.await().state.currentScene.value

@KorgeInternal
@KorgeExperimental
suspend fun GameScene.sceneView() = with(sceneView) {
    fun sceneMouseControl(contentContainer: View) {
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

    fun tokenPlacing() {
        onEvent(TokenPlacedUIEvent) {
            launch {
                gameSceneContainer.await().apply {
                    tokenView(it.event.token)
                }
            }
        }
    }

    container {
        name = "gameSceneContainer"

        onEvent(SceneOpenedUIEvent) {
            removeChildren()

            launch {
                val contentContainer = container {
                    name = "contentContainer"

                    it.scene.foregroundImagePath?.let {
                        image(applicationVfs[it].readBitmap())
                    }

                    tokenContainer.complete(container {
                        name = "tokenContainer"
                    })
                }.also { contentContainer.complete(it) }

                tokenPlacing()

                sceneMouseControl(contentContainer)
            }
        }
    }
}

