package tabletop.client.game.library

import arrow.core.raise.either
import dev.fritz2.core.RenderContext
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import tabletop.client.di.Dependencies
import tabletop.client.ui.window
import tabletop.common.dnd5e.DnD5e
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.error.CommonError
import tabletop.common.event.SceneOpened

class Library(val dependencies: Dependencies) {

    private val libraryOpenedStore = storeOf(false, Job())

    fun RenderContext.libraryButton() =
        button {
            clicks.map { !libraryOpenedStore.current } handledBy libraryOpenedStore.update
        }


    suspend fun RenderContext.libraryWindow() = either<CommonError, Unit> {
        with(dependencies.state) {
            window("Library") {
                fun RenderContext.scenesListing() {
                    h3 { +"Scenes" }
                    ul {
                        game.bind()
                            .scenes.bind().data.render { scenes ->
                                scenes.map { scene ->
                                    li {
                                        button {
                                            +scene.name

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
                                            button {
                                                +npc.name
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
                                            button {
                                                +pc.name
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
