package tabletop.client.game.library

import arrow.core.raise.either
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import tabletop.client.di.Dependencies
import tabletop.client.ui.customButton
import tabletop.client.ui.window
import tabletop.common.dnd5e.DnD5e
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.error.CommonError
import tabletop.common.event.SceneOpened

class Library(val dependencies: Dependencies) {

    private val libraryOpenedStore = storeOf(false, Job())
    private val logger = KotlinLogging.logger {  }

    fun RenderContext.libraryButton() =
        div("absolute top-1 left-1 flex inline items-center justify-center gap-1 text-white") {
            i( "ra ra-ball ra-5x") {}
            +"Library"
            clicks.map { !libraryOpenedStore.current } handledBy libraryOpenedStore.update
        }


    suspend fun RenderContext.libraryWindow() = either<CommonError, Unit> {
        libraryOpenedStore.data.render { opened ->
            if (opened) {
                with(dependencies.state) {
                    window("Library") {
                        fun RenderContext.scenesListing() {
                            h3 { +"Scenes" }
                            ul {
                                game.bind()
                                    .also { logger.debug {it.current.toString()} }
                                    .scenes.bind().data.render { scenes ->
                                        scenes.map { scene ->
                                            li {
                                                customButton(scene.name) {
                                                    clicks handledBy {
                                                        with(dependencies.eventHandler) {
                                                            SceneOpened(scene.id).handle()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                        }

                        fun RenderContext.nonPlayerCharactersListing() = either {
                            h3 { +"Non Player Characters" }
                            ul {
                                dependencies.state.run {
                                    game<DnD5e, DnD5eGame>().bind()
                                        .nonPlayerCharacters
                                        .data.render {
                                            it.map { npc ->
                                                li {
                                                    customButton(npc.name) {
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }


                        fun RenderContext.playerCharactersListing() {
                            h3 { +"Player Characters" }
                            ul {
                                dependencies.state.run {
                                    game<DnD5e, DnD5eGame>().bind()
                                        .nonPlayerCharacters
                                        .data.render {
                                            it.map { pc ->
                                                li {
                                                    customButton(pc.name) {
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }

                        scenesListing()
                        nonPlayerCharactersListing()
                        playerCharactersListing()
                    }
                }
            }
        }
    }
}
