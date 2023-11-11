package tabletop.common.game.player

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.user.User

class Player(
    override val name: String,
    val user: User,
    override val id: UUID = UUID.generateUUID()
) : User.Role, NamedEntity()