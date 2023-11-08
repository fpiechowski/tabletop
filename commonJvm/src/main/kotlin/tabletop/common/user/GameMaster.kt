package tabletop.common.user

import tabletop.common.NamedEntity
import java.util.*

class GameMaster(
    override val name: String,
    val user: User,
    override val id: UUID = UUID.randomUUID()
) : NamedEntity()
