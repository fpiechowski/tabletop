package tabletop.common.system

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.NamedEntity

@Serializable
abstract class System : NamedEntity() {
    abstract override val name: String
    abstract override val id: UUID
}
