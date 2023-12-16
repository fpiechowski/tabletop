package tabletop.common.scene

import arrow.core.Either
import arrow.core.raise.either
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.Entity
import tabletop.common.error.CommonError
import tabletop.common.scene.token.Token

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
