package tabletop.common.system

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity

open class System(override val name: String, override val id: UUID = UUID.generateUUID()) : NamedEntity()
