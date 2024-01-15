package tabletop.shared.game

import arrow.optics.Lens
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.shared.entity.Entity
import tabletop.shared.game.player.Player
import tabletop.shared.idOptional
import tabletop.shared.plus
import tabletop.shared.scene.Scene
import tabletop.shared.scene.token.TokenizableEntity
import tabletop.shared.system.System
import tabletop.shared.user.GameMaster

typealias Scenes = Map<UUID, Scene>

@Serializable
data class Game<T : System>(
    override val id: UUID,
    override val name: String,
    val system: T,
    val gameMaster: GameMaster,
    override val image: String? = null,
    val players: Map<UUID, Player> = mapOf(),
    val scenes: Map<UUID, Scene> = mapOf(),
    val tokenizableEntities: Map<UUID, TokenizableEntity> = mapOf(),
) : Entity() {

    val entities get() = players + scenes + tokenizableEntities + system + gameMaster + system.entities

    companion object {
        fun <T : System> system(): Lens<Game<T>, T> =
            Lens(
                get = { it.system },
                set = { game, system -> game.copy(system = system) }
            )

        val scenes: Lens<Game<*>, Scenes> =
            Lens(
                get = { game -> game.scenes },
                set = { game, scenes ->
                    game.copy(scenes = scenes)
                }
            )

        fun scene(id: UUID) =
            scenes compose idOptional<Scene>(id)

    }
}
