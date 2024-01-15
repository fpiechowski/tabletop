package tabletop.shared.dnd5e.character

import arrow.optics.Lens
import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.Usable
import tabletop.shared.dnd5e.IntModifier
import tabletop.shared.dnd5e.Modifier
import tabletop.shared.dnd5e.item.Equippable
import tabletop.shared.dnd5e.item.Item
import tabletop.shared.dnd5e.magic.Spell
import tabletop.shared.entity.Identifiable
import tabletop.shared.entity.Named
import tabletop.shared.game.player.Player
import tabletop.shared.geometry.Point
import tabletop.shared.scene.Scene
import tabletop.shared.scene.token.Token
import tabletop.shared.scene.token.Tokenizable
import tabletop.shared.scene.token.TokenizableEntity
import kotlin.math.floor

@Serializable
data class Character(
    val hp: Int,
    val currentHp: Int,
    val race: Race,
    val characterClassesLevels: Set<CharacterClassLevel> = setOf(),
    val skillProficiencies: Set<SkillProficiency> = setOf(),
    val savingThrowProficiencies: Set<SavingThrowProficiency> = setOf(),
    val attributes: Attributes = Attributes(),
    val equipment: Equipment = Equipment(),
    val experience: Long = 0,
    val features: Set<Feature> = setOf(),
    val spells: Set<Spell> = setOf(),
    val items: Set<Item> = setOf(),
    val player: Player? = null,
    override val tokenImageFilePath: String,
    override val name: String,
    override val image: String? = null,
    override val id: UUID
) : TokenizableEntity(), Tokenizable, Named, Identifiable<UUID>, Usable.User,
    Usable.Target {

    companion object {
        const val defaultImage = "assets/images/characters/default.png"

        val attributes: Lens<Character, Attributes> = Lens(
            get = { it.attributes },
            set = { character, attributes -> character.copy(attributes = attributes) }
        )
    }

    override fun tokenize(scene: Scene, position: Point): Token<Character> = Token(
        name,
        position,
        scene.gameId,
        scene.id,
        this.id,
        tokenImageFilePath
    )


    @Serializable
    data class Equipment(
        val armor: Equippable? = null,
        val helmet: Equippable? = null,
        val gloves: Equippable? = null,
        val boots: Equippable? = null,
        val ring1: Equippable? = null,
        val ring2: Equippable? = null,
        val mainHandWeapon: Equippable? = null,
        val secondaryHandWeapon: Equippable? = null,
    )

    @Serializable
    data class CharacterClassLevel(val level: Int, val characterClass: CharacterClass)

    val level: Int get() = characterClassesLevels.map { it.level }.reduce { acc, level -> acc + level }

    val armorClass: ArmorClass
        get() = ArmorClass(
            modifiers = listOf()
        )

    @Serializable
    data class Attributes(
        val strength: Int = 10,
        val dexterity: Int = 10,
        val constitution: Int = 10,
        val intelligence: Int = 10,
        val wisdom: Int = 10,
        val charisma: Int = 10,
    ) : Map<Attribute, Int> by mapOf(
        Attribute.Strength to strength,
        Attribute.Dexterity to dexterity,
        Attribute.Constitution to constitution,
        Attribute.Intelligence to intelligence,
        Attribute.Wisdom to wisdom,
        Attribute.Charisma to charisma
    ) {
        companion object {

            fun lensFor(attribute: Attribute): Lens<Attributes, Int> = Lens(
                get = { attributes -> attributes[attribute] ?: 0 },
                set = { attributes, value ->
                    when (attribute) {
                        Attribute.Strength -> attributes.copy(strength = value)
                        Attribute.Dexterity -> attributes.copy(dexterity = value)
                        Attribute.Constitution -> attributes.copy(constitution = value)
                        Attribute.Intelligence -> attributes.copy(intelligence = value)
                        Attribute.Wisdom -> attributes.copy(wisdom = value)
                        Attribute.Charisma -> attributes.copy(charisma = value)
                    }
                }
            )
        }
    }

    class ArmorClass(
        val modifiers: List<Modifier<Int>>
    ) {
        val value: Int get() = modifiers.fold(10) { acc, modifier -> with(modifier) { acc.modify() } }
    }

    enum class Alignment {
        Neutral;


    }

    enum class Size {
        Tiny,
        Small,
        Medium,
        Large,
        Huge,
        Gargantuan;
    }

    @Serializable
    sealed class Attribute(override val name: String, val shortName: String) : Proficiency.Subject {

        data object Strength : Attribute("Strength", "str")
        data object Dexterity : Attribute("Dexterity", "dex")
        data object Constitution : Attribute("Constitution", "con")
        data object Intelligence : Attribute("Intelligence", "int")
        data object Wisdom : Attribute("Wisdom", "wis")
        data object Charisma : Attribute("Charisma", "cha")

        companion object {
            fun modifier(value: Int): IntModifier = IntModifier { floor((value - 10).toFloat() / 2f).toInt() }
        }
    }
}

