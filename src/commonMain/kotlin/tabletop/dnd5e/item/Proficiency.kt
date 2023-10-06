package tabletop.dnd5e.item

import tabletop.Item
import tabletop.dnd5e.DnD5eCharacter
import tabletop.dnd5e.Modifier

abstract class Proficiency<T : Proficiency.Subject>(
    override val name: String,
    open val subject: T,
    val proficiencyModifier: Modifier<Int>
) : Item<DnD5eCharacter, Nothing>() {

    interface Subject
}

