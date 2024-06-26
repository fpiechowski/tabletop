package tabletop.shared.user

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity

@Serializable
data class User(
    override val name: String,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID(),
) : Entity() {

    interface Role
}
