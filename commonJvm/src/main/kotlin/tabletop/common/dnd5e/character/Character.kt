package tabletop.common.dnd5e.character

import tabletop.common.Identifiable
import tabletop.common.Named
import tabletop.common.Usable
import tabletop.common.dnd5e.Modifier
import tabletop.common.dnd5e.item.Equippable
import tabletop.common.game.player.Player
import tabletop.common.geometry.Point
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token
import tabletop.common.scene.token.Tokenizable
import java.io.Serializable
import java.util.*
import kotlin.math.floor

class PlayerCharacter(
    override val hp: Int,
    override val race: Race,
    override val characterClasses: Set<CharacterClass>,
    override val skillProficiencies: Set<SkillProficiency>,
    override val attributes: Character.Attributes,
    override val equipped: Set<Equippable>,
    override val name: String,
    override val tokenImageFilePath: String,
    val player: Player,
    override val id: UUID = UUID.randomUUID()
) : Character, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    override fun tokenize(scene: Scene, position: Point): Token<*> {
        TODO("Not yet implemented")
    }
}

class NonPlayerCharacter(
    override val hp: Int,
    override val race: Race,
    override val characterClasses: Set<CharacterClass>,
    override val skillProficiencies: Set<SkillProficiency>,
    override val attributes: Character.Attributes,
    override val equipped: Set<Equippable>,
    override val name: String,
    override val tokenImageFilePath: String,
    override val id: UUID = UUID.randomUUID()
) : Character, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }


    override fun tokenize(scene: Scene, position: Point): Token<NonPlayerCharacter> = Token(
        name,
        position,
        scene,
        this,
        tokenImageFilePath
    )
}

interface Character : Tokenizable, Named, Identifiable<UUID>, Usable.User,
    Usable.Target, Serializable {
    override val tokenImageFilePath: String
    override val name: String
    val hp: Int
    val race: Race
    val characterClasses: Set<CharacterClass>
    val skillProficiencies: Set<SkillProficiency>
    val attributes: Attributes
    val equipped: Set<Equippable>
    override val id: UUID

    val level: Int get() = characterClasses.map { it.level }.reduce { acc, level -> acc + level }

    val armorClass: ArmorClass
        get() = ArmorClass(
            modifiers = listOf(
                Attribute.Dexterity.modifier(attributes.dexterity)
            )
        )

    class Attributes(
        val strength: Int = 10,
        val dexterity: Int = 10,
        val constitution: Int = 10,
        val intelligence: Int = 10,
        val wisdom: Int = 10,
        val charisma: Int = 10
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }

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

    enum class Alignment : Serializable {
        Neutral;

        companion object {
            private const val serialVersionUID = 1L
        }
    }

    enum class Size : Serializable {
        Tiny,
        Small,
        Medium,
        Large,
        Huge,
        Gargantuan;

        companion object {
            private const val serialVersionUID = 1L
        }
    }

    enum class Attribute : Serializable {

        Strength,
        Dexterity,
        Constitution,
        Intelligence,
        Wisdom,
        Charisma;

        companion object {
            private const val serialVersionUID = 1L
        }

        fun modifier(value: Int): Modifier<Int> = Modifier { floor((value - 10).toFloat() / 2f).toInt() }
    }
}

enum class Skill : Proficiency.Subject, Serializable {
    Stealth;

    companion object {
        private const val serialVersionUID = 1L
    }
}

class SkillProficiency(
    override val name: String,
    override val subject: Skill,
    override val proficiencyModifier: Modifier<Int>,
) : Proficiency<Skill>(), Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
