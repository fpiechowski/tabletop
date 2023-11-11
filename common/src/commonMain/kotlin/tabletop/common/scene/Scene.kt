package tabletop.common.scene

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.scene.token.Token

class Scene(
    override val name: String,
    val foregroundImagePath: String?,
    val tokens: MutableMap<UUID, Token<*>> = mutableMapOf(),
    val grid: Grid? = null,
    override val id: UUID = UUID.generateUUID()
) : NamedEntity() {


    class Grid(
        val size: Int
    )
}
