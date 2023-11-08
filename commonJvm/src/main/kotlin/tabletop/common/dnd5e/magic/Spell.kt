package tabletop.common.dnd5e.magic

import tabletop.common.NamedEntity
import java.util.*

class Spell(override val name: String, override val id: UUID = UUID.randomUUID()) : NamedEntity()
