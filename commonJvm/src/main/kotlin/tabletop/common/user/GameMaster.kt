package tabletop.common.user

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

@Serializable
class GameMaster(
    override val name: String,
    val user: User,
    override val id: UUID = UUID.generateUUID()
) : NamedEntity()
