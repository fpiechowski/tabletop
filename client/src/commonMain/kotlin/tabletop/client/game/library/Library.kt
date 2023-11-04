package tabletop.client.game.library

import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.input.draggableCloseable
import korlibs.korge.input.mouse
import korlibs.korge.input.onClick
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.centerOnStage
import korlibs.math.geom.Size
import tabletop.client.di.Dependencies
import tabletop.client.event.SceneOpened
import tabletop.client.game.GameScene
import tabletop.client.game.game
import tabletop.client.ui.uiScaling
import tabletop.common.rpg.RolePlayingGame

@KorgeInternal
@KorgeExperimental
fun GameScene.libraryButton() = with(sceneView) {
    uiButton("Library") {
        onClick {
            libraryWindow()
        }

        width = 100f
        height = 30f

        uiScaling()
    }.xy(20, 20)
}

@KorgeInternal
@KorgeExperimental
suspend fun GameScene.libraryWindow() = with(sceneView) {
    uiWindow("Library", Size(800, 600)) { window ->
        val game = game()

        uiVerticalStack {
            uiText("Scenes")
            uiGridFill(cols = 4) {
                val scenes = game.scenes.values
                scenes.map { scene ->
                    uiButton(scene.name) {
                        onClick {
                            with(Dependencies.await().eventHandler) {
                                SceneOpened(scene).handle()
                            }
                        }
                    }
                }
            }

            when (game) {
                is RolePlayingGame -> {
                    uiText("Non Player Characters")
                    uiGridFill(cols = 4) {
                        game.nonPlayerCharacters
                            .map { character ->
                                uiButton(character.name) {
                                    var dragging = false
                                    val tokenImage = Image(applicationVfs[character.tokenImageFilePath].readBitmap())
                                        .alpha(0.6)
                                        .zIndex(1)

                                    mouse.onDownCloseable {
                                        if (it.button.isLeft) {
                                            dragging = true
                                            tokenImage.addTo(sceneView)
                                        }
                                    }
                                    sceneView.mouse.onUpCloseable {
                                        if (it.button.isLeft && dragging) {
                                            dragging = false
                                            tokenImage.removeFromParent()
                                        }
                                    }
                                    draggableCloseable(autoMove = false) {
                                        if (dragging) {
                                            tokenImage.globalPos = it.mouseEvents.currentPosGlobal
                                        }
                                    }
                                }
                            }
                    }
                }
            }

        }

    }.centerOnStage()
}