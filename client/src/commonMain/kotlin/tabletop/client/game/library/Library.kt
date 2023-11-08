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
import korlibs.math.geom.Vector2
import tabletop.client.di.Dependencies
import tabletop.client.game.GameScene
import tabletop.client.scene.currentScene
import tabletop.client.ui.uiScaling
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.event.SceneOpened
import tabletop.common.event.TokenPlacingRequested
import tabletop.common.game.Game
import tabletop.common.geometry.Point
import tabletop.common.scene.token.Tokenizable

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
    Dependencies.await().state.game.value?.let { game ->

        uiWindow("Library", Size(800, 600)) { window ->

            fun Container.scenesListing(game: Game<*>) {
                uiText("Scenes")
                uiGridFill(cols = 4) {
                    width = 800f
                    val scenes = game.scenes.values
                    scenes.map { scene ->
                        uiButton(scene.name) {
                            onClick {
                                with(Dependencies.await().eventHandler) {
                                    SceneOpened(scene.id).handle()
                                }
                            }
                        }
                    }
                }
            }

            suspend fun View.draggableTokenizable(tokenizable: Tokenizable) {
                var dragging = false

                val tokenImage = Image(applicationVfs[tokenizable.tokenImageFilePath].readBitmap())
                    .apply {
                        val contentScale = contentView.await().scale
                        alpha(0.6)
                        zIndex(1)
                        scale(contentScale.scaleX, contentScale.scaleY)
                    }

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
                        with(Dependencies.await().eventHandler) {
                            currentScene()?.let { scene ->
                                val tokenContainerView = contentView.await()
                                TokenPlacingRequested(
                                    game.id,
                                    tokenizable.id,
                                    scene.id,
                                    tokenContainerView.globalToLocal(
                                        it.currentPosGlobal
                                    ).toCommon()
                                ).handle()
                            }
                        }
                    }
                }
                draggableCloseable(autoMove = false) {
                    if (dragging) {
                        tokenImage.globalPos = it.mouseEvents.currentPosGlobal
                    }
                }
            }

            @KorgeInternal
            suspend fun Container.nonPlayerCharactersListing(game: DnD5eGame) {
                this.uiText("Non Player Characters")
                this.uiGridFill(cols = 4) {
                    width = 800f
                    game.nonPlayerCharacters
                        .map { npc ->
                            uiButton(npc.name).draggableTokenizable(npc)
                        }
                }
            }


            @KorgeInternal
            suspend fun Container.playerCharactersListing(game: DnD5eGame) {
                this.uiText("Player Characters")
                this.uiGridFill(cols = 4) {
                    width = 800f
                    game.nonPlayerCharacters
                        .map { tokenizable ->
                            uiButton(tokenizable.name).draggableTokenizable(tokenizable)
                        }
                }
            }


            uiVerticalStack {
                game.let { this@uiVerticalStack.scenesListing(it) }

                when (game) {
                    is DnD5eGame -> {
                        this@uiVerticalStack.nonPlayerCharactersListing(game)
                        this@uiVerticalStack.playerCharactersListing(game)
                    }
                }
            }

        }.centerOnStage()
    }

}

private fun Vector2.toCommon(): Point = Point(x.toInt(), y.toInt())

