package tabletop.client.game

import korlibs.korge.scene.Scene
import korlibs.korge.ui.uiText
import korlibs.korge.view.SContainer
import korlibs.korge.view.align.centerOnStage
import tabletop.common.Game

class GameScene(game: Game) : Scene() {
    override suspend fun SContainer.sceneMain() {
        uiText("GameScene").centerOnStage()
    }
}