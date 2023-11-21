package tabletop.common.user

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.NamedEntity

@Serializable
data class User(
    override val name: String,
    override val id: UUID = UUID.generateUUID(),
) : NamedEntity() {

    interface Role
}
