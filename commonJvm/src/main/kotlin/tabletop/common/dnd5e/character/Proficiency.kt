package tabletop.common.dnd5e.character

import tabletop.common.Named
import tabletop.common.dnd5e.Modifier

abstract class Proficiency<T : Proficiency.Subject>(
    override val name: String,
    open val subject: T,
    val proficiencyModifier: Modifier<Int>
) : Named {

    interface Subject
}

