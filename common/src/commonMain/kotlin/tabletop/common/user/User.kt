package tabletop.common.user

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

data class User(
    override val name: String,
    override val id: UUID = UUID.generateUUID(),
) : NamedEntity() {

    interface Role
}

