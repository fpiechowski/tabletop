package tabletop.common.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dnd5e.DnD5eCharacter
import tabletop.common.rpg.item.Item

class Spell(override val name: String, override val id: UUID = UUID.generateUUID()) : Item<DnD5eCharacter, Nothing>()
