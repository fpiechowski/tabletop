package tabletop.common.dnd5e

import tabletop.common.dnd5e.item.Language
import tabletop.common.dnd5e.item.Proficiency
import tabletop.common.rpg.item.Item
import tabletop.common.scene.Token

abstract class Background(
    val proficiencies: Set<Proficiency<*>>,
    val languages: Set<Language>,
    val equipment: Set<Item<DnD5eCharacter, Token<*>>>
) : Item<DnD5eCharacter, Token<*>>()
