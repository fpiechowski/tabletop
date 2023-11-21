package tabletop.common.game.player

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.NamedEntity
import tabletop.common.user.User

@Serializable
data class Player(
    override val name: String,
    val gameId: UUID,
    val user: User,
    override val id: UUID = UUID.generateUUID()
) : User.Role, NamedEntity()
