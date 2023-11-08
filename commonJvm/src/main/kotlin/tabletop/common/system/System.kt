package tabletop.common.system

import tabletop.common.NamedEntity
import java.util.*

open class System(override val name: String, override val id: UUID = UUID.randomUUID()) : NamedEntity()
