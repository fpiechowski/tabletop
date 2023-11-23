package tabletop.client.scene

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import io.nacular.doodle.application.Modules.Companion.ImageModule
import io.nacular.doodle.application.application
import io.nacular.doodle.controls.Photo
import io.nacular.doodle.core.Display
import io.nacular.doodle.image.ImageLoader
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.instance
import org.w3c.dom.HTMLDivElement
import tabletop.client.di.Dependencies
import tabletop.client.graphics.GraphicsComponent
import kotlin.coroutines.CoroutineContext

suspend fun currentScene() = Dependencies.instance.await().state.currentScene.current

fun RenderContext.scene(dependencies: Dependencies) = SceneView(dependencies).run { render() }

class SceneView(val dependencies: Dependencies): CoroutineScope {
    val tokensDiv = CompletableDeferred<HtmlTag<HTMLDivElement>>()

    override val coroutineContext: CoroutineContext get() = dependencies.userInterface.coroutineContext

    fun RenderContext.render() = div("w-full h-full") {
        application(this.domNode, modules = listOf(ImageModule)) {
            SceneGraphicsComponent(
                dependencies,
                display = instance(),
                imageLoader = instance(),
                coroutineScope = this@SceneView
            )
        }
    }

    class SceneGraphicsComponent(
        val dependencies: Dependencies,
        display: Display,
        imageLoader: ImageLoader,
        coroutineScope: CoroutineScope
    ) : GraphicsComponent {
        init {
            dependencies.state.currentScene.current?.let { currentScene ->
                currentScene.foregroundImagePath?.let { foregroundImagePath ->
                    coroutineScope.launch {
                        imageLoader.load(foregroundImagePath)?.let {
                            display += Photo(it)
                        }
                    }
                }
            }
        }

        override fun shutdown() {
            TODO("Not yet implemented")
        }
    }
}
