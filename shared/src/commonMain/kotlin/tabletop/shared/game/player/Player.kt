package tabletop.shared.game.player

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity
import tabletop.shared.user.User

@Serializable
data class Player(
    override val name: String,
    val gameId: UUID,
    val user: User,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : User.Role, Entity()
