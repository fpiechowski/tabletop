package tabletop.common.dnd5e

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Usable
import tabletop.common.dnd5e.item.CharacterClass
import tabletop.common.dnd5e.item.Proficiency
import tabletop.common.dnd5e.item.Race
import tabletop.common.dnd5e.item.inventory.Equippable
import tabletop.common.dnd5e.item.inventory.Weapon
import tabletop.common.rpg.character.Character
import tabletop.common.scene.Token

data class DnD5eCharacter(
    override val id: UUID,
    val hp: Int,
    val race: Race,
    val characterClasses: Set<CharacterClass>,
    val skillProficiencies: Set<SkillProficiency>,
    override val name: String,
    val attributes: Attributes,
    val equipped: Set<Equippable>,
) : Character(name), Usable.User, Usable.Targetable {
    fun attack(weapon: Weapon, targets: Set<Token<DnD5eCharacter>>) {

    }

    val level: Int = characterClasses.map { it.level }.reduce { acc, level -> acc + level }


    val armorClass: ArmorClass
        get() = ArmorClass(
            modifiers = listOf(
                Attribute.Dexterity.modifier(attributes.dexterity)
            )
        )

    class Attributes(
        val strength: Int,
        val dexterity: Int,
        val constitution: Int,
        val intelligence: Int,
        val wisdom: Int,
        val charisma: Int
    ) {
        operator fun get(attribute: Attribute) = when (attribute) {
            Attribute.Strength -> strength
            Attribute.Dexterity -> dexterity
            Attribute.Constitution -> constitution
            Attribute.Intelligence -> intelligence
            Attribute.Wisdom -> wisdom
            Attribute.Charisma -> charisma
        }
    }

    class ArmorClass(
        val modifiers: List<Modifier<Int>>
    ) {
        val value: Int get() = modifiers.fold(10) { acc, modifier -> with(modifier) { acc.modify() } }
    }

    enum class Alignment

    enum class Size

    enum class Attribute {
        Strength,
        Dexterity,
        Constitution,
        Intelligence,
        Wisdom,
        Charisma;

        fun modifier(value: Int): Modifier<Int> = TODO()
    }
}

enum class Skill : Proficiency.Subject

class SkillProficiency(
    override val name: String,
    override val subject: Skill,
    val modifier: Modifier<Int>,
    override val id: UUID = UUID.generateUUID()
) : Proficiency<Skill>(name, subject, modifier)
