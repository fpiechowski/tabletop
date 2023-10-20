package tabletop.common.user

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

@Serializable
class Player(
    override val name: String,
    val user: User,
    override val id: UUID = UUID.generateUUID()
) : User.Role, NamedEntity()
