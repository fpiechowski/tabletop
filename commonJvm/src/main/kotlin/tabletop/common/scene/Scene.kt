package tabletop.common.scene

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

@Serializable
class Scene(
    override val name: String,
    val foregroundImagePath: String?,
    val tokens: MutableMap<UUID, Token<*>> = mutableMapOf(),
    override val id: UUID = UUID.generateUUID()
) : NamedEntity()
