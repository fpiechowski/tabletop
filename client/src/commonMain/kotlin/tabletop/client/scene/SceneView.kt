package tabletop.client.scene

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.lensOf
import io.nacular.doodle.application.application
import kotlinx.coroutines.CompletableDeferred
import kotlinx.uuid.UUID
import org.w3c.dom.HTMLDivElement
import tabletop.client.di.Dependencies
import tabletop.client.graphics.GraphicsComponent
import tabletop.common.scene.token.Token

suspend fun currentScene() = Dependencies.instance.await().state.currentScene.current

fun RenderContext.scene(dependencies: Dependencies) = SceneView(dependencies).run { render() }

class SceneView(val dependencies: Dependencies) {
    val tokensDiv = CompletableDeferred<HtmlTag<HTMLDivElement>>()
    val graphicsComponent = SceneGraphicsComponent()

    fun RenderContext.render() = div("w-full h-full") {
        application(this.domNode) {
            SceneGraphicsComponent()
        }
    }

    class SceneGraphicsComponent : GraphicsComponent {
        init {

        }

        override fun shutdown() {
            TODO("Not yet implemented")
        }

    }
}
