package tabletop.client.game

import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import tabletop.client.di.Dependencies
import tabletop.client.game.library.Library
import tabletop.client.scene.SceneView
import tabletop.client.scene.scene

fun RenderContext.gameScene(dependencies: Dependencies) = GameScene(dependencies).run { render() }

class GameScene(
    val dependencies: Dependencies
) {
    val zoomScaleStore = storeOf(1.0f, Job())
    val library = Library(dependencies)
    val sceneView = SceneView(dependencies)


    fun RenderContext.render() {
        with(sceneView) {
            scene(dependencies)
        }

        with(library) {
            libraryButton()
        }
    }
}