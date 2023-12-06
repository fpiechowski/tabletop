package tabletop.common.game

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import arrow.optics.Lens
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.common.dnd5e.DnD5e
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.entity.Entity
import tabletop.common.error.CommonError
import tabletop.common.error.UnsupportedSubtypeError
import tabletop.common.game.player.Player
import tabletop.common.idLens
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Tokenizable
import tabletop.common.system.System
import tabletop.common.user.GameMaster

@Serializable
abstract class Game<T : System> : Entity() {

    abstract override val id: UUID
    abstract val system: T
    abstract val gameMaster: GameMaster
    abstract val players: Map<UUID, Player>
    abstract val scenes: Map<UUID, Scene>
    abstract val tokenizables: Map<UUID, Tokenizable>
    abstract val chat: Chat
    abstract val entities: Map<UUID, Entity>


    class Chat {
        interface Speaker

        fun <T> send(speaker: Speaker, message: T): Unit = TODO()
    }

    companion object {
        val scenes: Either<CommonError, Lens<Game<*>, Map<UUID, Scene>>> = either {
            Lens(
                get = { game -> game.scenes },
                set = { game, scenes ->
                    when (game) {
                        is DnD5eGame -> game.copy(scenes = scenes)
                        else -> raise(UnsupportedSubtypeError(Game::class))
                    }
                }
            )
        }

        fun scene(id: UUID) = either {
            scenes.bind()
                .compose(idLens<Scene>(id).bind())
        }


        @Suppress("UNCHECKED_CAST")
        fun <T : System> system(): Either<CommonError, Lens<Game<T>, T>> = either {
            Lens(
                get = { game -> game.system },
                set = { game, system ->
                    when (game) {
                        is DnD5eGame -> ensure(system is DnD5e) { UnsupportedSubtypeError(System::class) }
                            .let { game.copy(system = system) } as Game<T>

                        else -> raise(UnsupportedSubtypeError(Game::class))
                    }
                }
            )
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T : System, G : Game<T>> withSystem(): Either<CommonError, Lens<Game<*>, G>> = either {
            Lens(
                get = { game -> game.also { ensure(game.system is T) { UnsupportedSubtypeError(System::class) } } as G },
                set = { _, gameWithSystem -> gameWithSystem }
            )
        }
    }
}
