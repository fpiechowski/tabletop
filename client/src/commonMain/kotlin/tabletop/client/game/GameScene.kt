package tabletop.client.game

import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.view.SContainer
import korlibs.korge.view.View
import kotlinx.coroutines.CompletableDeferred
import tabletop.client.game.library.libraryButton
import tabletop.client.scene.sceneView
import korlibs.korge.scene.Scene as UIScene


@KorgeExperimental
@KorgeInternal
class GameScene : UIScene() {

    val gameSceneView = CompletableDeferred<View>()

    override suspend fun SContainer.sceneMain() {
        sceneView()
            .also { gameSceneView.complete(it) }
        libraryButton()
    }
}