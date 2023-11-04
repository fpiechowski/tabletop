package tabletop.common.system

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

@Serializable
open class System(override val name: String, override val id: UUID = UUID.generateUUID()) : NamedEntity()
