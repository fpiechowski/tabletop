package tabletop.dnd5e.item

import tabletop.Item
import tabletop.dnd5e.DnD5eCharacter

class Spell(override val name: String) : Item<DnD5eCharacter, Nothing>()
