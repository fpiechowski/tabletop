package tabletop.common.dnd5e.magic

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.NamedEntity

class Spell(override val name: String, override val id: UUID = UUID.generateUUID()) : NamedEntity()
