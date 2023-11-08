package tabletop.common.game.player

import tabletop.common.NamedEntity
import tabletop.common.user.User
import java.util.*

class Player(
    override val name: String,
    val user: User,
    override val id: UUID = UUID.randomUUID()
) : User.Role, NamedEntity()