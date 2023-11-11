package tabletop.common.user

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

class GameMaster(
    override val name: String,
    val user: User,
    override val id: UUID = UUID.generateUUID()
) : NamedEntity()
