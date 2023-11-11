package tabletop.common.dnd5e.character

import tabletop.common.Named
import tabletop.common.dnd5e.item.Item

abstract class Background(
    val proficiencies: Set<Proficiency<*>>,
    val languages: Set<Language>,
    val equipment: Set<Item>
) : Named
