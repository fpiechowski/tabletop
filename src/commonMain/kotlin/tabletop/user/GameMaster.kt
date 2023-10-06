package tabletop.user

import tabletop.Entity

class GameMaster(
    override val name: String,
    val user: User
) : Entity()
