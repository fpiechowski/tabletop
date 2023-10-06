package tabletop.dnd5e

import tabletop.Item
import tabletop.dnd5e.item.Language
import tabletop.dnd5e.item.Proficiency
import tabletop.scene.Token

abstract class Background(
    val proficiencies: Set<Proficiency<*>>,
    val languages: Set<Language>,
    val equipment: Set<Item<DnD5eCharacter, Token<*>>>
) : Item<DnD5eCharacter, Token<*>>()
