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
    abstract override val name: String
    abstract val system: T
    abstract val gameMaster: GameMaster
    abstract val players: Map<UUID, Player>
    abstract val scenes: Map<UUID, Scene>
    abstract val tokenizables: Map<UUID, Tokenizable>
    abstract val entities: Map<UUID, Entity>

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
    }
}
