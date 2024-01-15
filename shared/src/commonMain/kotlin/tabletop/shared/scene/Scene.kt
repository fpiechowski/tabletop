package tabletop.shared.scene

import arrow.optics.Lens
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity
import tabletop.shared.scene.token.Token

typealias Tokens = Map<UUID, Token<*>>

@Serializable
data class Scene(
    override val name: String,
    val gameId: UUID,
    val foregroundImagePath: String?,
    val tokens: Tokens = mapOf(),
    val grid: Grid? = null,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Entity() {

    companion object {
        val tokens: Lens<Scene, Tokens> = Lens(
            get = { it.tokens },
            set = { scene, tokens -> scene.copy(tokens = tokens) }
        )
    }

    @Serializable
    class Grid(
        val size: Int
    )
}
