package tabletop.client.game

import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.view.Container
import korlibs.korge.view.SContainer
import korlibs.korge.view.View
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.game.library.libraryButton
import tabletop.client.scene.sceneView
import korlibs.korge.scene.Scene as UIScene


@KorgeExperimental
@KorgeInternal
class GameScene : UIScene() {
    val gameSceneContainer = CompletableDeferred<Container>()
    val contentContainer = CompletableDeferred<Container>()
    val tokenContainer = CompletableDeferred<Container>()
    val tokenViews = MutableStateFlow(listOf<View>())

    override suspend fun SContainer.sceneMain() {
        sceneView()
            .also { gameSceneContainer.complete(it) }
        libraryButton()
    }
}