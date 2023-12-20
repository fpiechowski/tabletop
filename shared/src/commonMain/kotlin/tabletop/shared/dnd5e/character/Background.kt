package tabletop.shared.dnd5e.character

import tabletop.shared.entity.Named
import tabletop.shared.dnd5e.item.Item

abstract class Background(
    val proficiencies: Set<Proficiency<*>>,
    val languages: Set<Language>,
    val equipment: Set<Item>
) : Named
