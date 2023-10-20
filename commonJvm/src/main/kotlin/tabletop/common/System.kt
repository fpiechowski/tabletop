package tabletop.common

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

@Serializable
open class System(override val name: String, override val id: UUID = UUID.generateUUID()) : NamedEntity()
