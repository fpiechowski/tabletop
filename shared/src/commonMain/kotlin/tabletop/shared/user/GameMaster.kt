package tabletop.shared.user

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity

@Serializable
class GameMaster(
    override val name: String,
    val gameId: UUID,
    val user: User,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Entity(), User.Role
