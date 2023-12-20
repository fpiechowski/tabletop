package tabletop.shared.scene

import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity
import tabletop.shared.scene.token.Token

@Serializable
@optics
data class Scene(
    override val name: String,
    val gameId: UUID,
    val foregroundImagePath: String?,
    val tokens: Map<UUID, Token<*>> = mapOf(),
    val grid: Grid? = null,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Entity() {

    companion object;

    @Serializable
    class Grid(
        val size: Int
    )
}
