package tabletop.common.user

import tabletop.common.NamedEntity
import java.util.*

data class User(
    override val name: String,
    override val id: UUID = UUID.randomUUID(),
) : NamedEntity() {

    interface Role
}

