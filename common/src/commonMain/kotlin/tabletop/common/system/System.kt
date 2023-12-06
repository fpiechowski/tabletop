package tabletop.common.system

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.Entity

@Serializable
abstract class System : Entity() {
    abstract override val name: String
    abstract override val id: UUID
}
