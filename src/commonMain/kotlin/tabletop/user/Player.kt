package tabletop.user

import tabletop.Entity

class Player(
    override val name: String,
    val user: User
) : User.Role, Entity()
