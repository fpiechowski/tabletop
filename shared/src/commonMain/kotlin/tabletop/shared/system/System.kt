package tabletop.shared.system

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.shared.entity.Entity

@Serializable
abstract class System : Entity() {
    abstract override val name: String
    abstract override val id: UUID
}
