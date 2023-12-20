package tabletop.shared.dnd5e.character

import kotlinx.serialization.Serializable
import tabletop.shared.dnd5e.WeaponType
import tabletop.shared.dnd5e.skill.Skill
import tabletop.shared.entity.Named

@Serializable
abstract class Proficiency<T : Proficiency.Subject> : Named {

    abstract override val name: String
    abstract val subject: T

    interface Subject : Named
}

@Serializable
data class WeaponProficiency(
    override val subject: WeaponType
) : Proficiency<WeaponType>() {
    override val name = "${subject.name} Proficiency"
}

@Serializable
data class SkillProficiency(
    override val subject: Skill,
) : Proficiency<Skill>() {
    override val name: String = "${subject.name} Proficiency"

}

@Serializable
data class SavingThrowProficiency(
    override val subject: Character.Attribute,
) : Proficiency<Character.Attribute>() {
    override val name: String = "${subject.name} Saving Throw Proficiency"
}
