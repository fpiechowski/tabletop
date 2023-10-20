package tabletop.common.dnd5e.item

import tabletop.common.dnd5e.DnD5eCharacter
import tabletop.common.dnd5e.Modifier
import tabletop.common.rpg.item.Item

abstract class Proficiency<T : Proficiency.Subject>(
    override val name: String,
    open val subject: T,
    val proficiencyModifier: Modifier<Int>
) : Item<DnD5eCharacter, Nothing>() {

    interface Subject
}

