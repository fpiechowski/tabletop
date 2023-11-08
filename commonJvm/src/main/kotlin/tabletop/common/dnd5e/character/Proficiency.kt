package tabletop.common.dnd5e.character

import kotlinx.serialization.Serializable
import tabletop.common.Named
import tabletop.common.dnd5e.Modifier

@Serializable
abstract class Proficiency<T : Proficiency.Subject> : Named {

    abstract override val name: String
    abstract val subject: T
    abstract val proficiencyModifier: Modifier<Int>

    interface Subject
}

