package tabletop.client.game

import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.game.library.Library
import tabletop.client.scene.SceneView
import tabletop.client.scene.scene

fun RenderContext.gameScreen(dependencies: Dependencies) = GameScreen(dependencies).run { render() }

class GameScreen(
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
            dependencies.userInterface.launch { libraryWindow() }
        }
    }
}