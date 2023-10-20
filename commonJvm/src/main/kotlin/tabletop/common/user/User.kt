package tabletop.common.user

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.command.Command

@Serializable
data class User(
    override val name: String,
    override val id: UUID = UUID.generateUUID(),
) : NamedEntity(), Command.Result.Data {

    interface Role
}

