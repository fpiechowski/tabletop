package tabletop.shared.game

import arrow.core.Either
import arrow.core.raise.either
import arrow.optics.Lens
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.shared.dnd5e.DnD5eGame
import tabletop.shared.entity.Entity
import tabletop.shared.error.CommonError
import tabletop.shared.error.UnsupportedSubtypeError
import tabletop.shared.game.player.Player
import tabletop.shared.idLens
import tabletop.shared.scene.Scene
import tabletop.shared.scene.token.Tokenizable
import tabletop.shared.system.System
import tabletop.shared.user.GameMaster

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
