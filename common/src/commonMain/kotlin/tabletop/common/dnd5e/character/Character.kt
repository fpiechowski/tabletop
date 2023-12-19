package tabletop.common.dnd5e.character

import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Usable
import tabletop.common.dnd5e.IntModifier
import tabletop.common.dnd5e.Modifier
import tabletop.common.dnd5e.item.Equippable
import tabletop.common.dnd5e.item.Item
import tabletop.common.dnd5e.magic.Spell
import tabletop.common.entity.Identifiable
import tabletop.common.entity.Named
import tabletop.common.game.player.Player
import tabletop.common.geometry.Point
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token
import tabletop.common.scene.token.Tokenizable
import tabletop.common.scene.token.TokenizableEntity
import kotlin.math.floor


@Serializable
@optics
data class PlayerCharacter(
    override val hp: Int,
    override val currentHp: Int,
    override val race: Race,
    override val attributes: Attributes = Attributes(),
    override val characterClassesLevels: Set<CharacterClassLevel> = setOf(),
    override val skillProficiencies: Set<SkillProficiency> = setOf(),
    override val equipment: Equipment = Equipment(),
    override val name: String,
    override val tokenImageFilePath: String,
    val player: Player,
    override val image: String? = null,
    override val experience: Long = 0,
    override val savingThrowProficiencies: Set<SavingThrowProficiency> = setOf(),
    override val features: Set<Feature> = setOf(),
    override val spells: Set<Spell> = setOf(),
    override val items: Set<Item> = setOf(),
    override val id: UUID = UUID.generateUUID(),
) : Character() {

    companion object;

    override fun tokenize(scene: Scene, position: Point): Token<*> {
        TODO("Not yet implemented")
    }
}

@Serializable
@optics
data class NonPlayerCharacter(
    override val hp: Int,
    override val currentHp: Int,
    override val race: Race,
    override val characterClassesLevels: Set<CharacterClassLevel> = setOf(),
    override val skillProficiencies: Set<SkillProficiency> = setOf(),
    override val attributes: Attributes = Attributes(),
    override val equipment: Equipment = Equipment(),
    override val name: String,
    override val tokenImageFilePath: String,
    override val image: String? = null,
    override val experience: Long = 0,
    override val savingThrowProficiencies: Set<SavingThrowProficiency> = setOf(),
    override val features: Set<Feature> = setOf(),
    override val spells: Set<Spell> = setOf(),
    override val items: Set<Item> = setOf(),
    override val id: UUID = UUID.generateUUID(),
) : Character() {

    companion object;

    override fun tokenize(scene: Scene, position: Point): Token<NonPlayerCharacter> = Token(
        name,
        position,
        scene.gameId,
        scene.id,
        this.id,
        tokenImageFilePath
    )
}

@Serializable
abstract class Character : TokenizableEntity(), Tokenizable, Named, Identifiable<UUID>, Usable.User,
    Usable.Target {
    abstract override val tokenImageFilePath: String
    abstract override val name: String
    abstract val hp: Int
    abstract val currentHp: Int
    abstract val race: Race
    abstract val characterClassesLevels: Set<CharacterClassLevel>
    abstract val skillProficiencies: Set<SkillProficiency>
    abstract val savingThrowProficiencies: Set<SavingThrowProficiency>
    abstract val attributes: Attributes
    abstract val equipment: Equipment
    abstract val experience: Long
    abstract val features: Set<Feature>
    abstract val spells: Set<Spell>
    abstract val items: Set<Item>

    abstract override val id: UUID

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
            modifiers = listOf(
                Attribute.modifier(attributes.dexterity)
            )
        )

    @Serializable
    class Attributes(
        val strength: Int = 10,
        val dexterity: Int = 10,
        val constitution: Int = 10,
        val intelligence: Int = 10,
        val wisdom: Int = 10,
        val charisma: Int = 10,
    ) : Map<Attribute, Int> by mapOf(
        Attribute.strength to strength,
        Attribute.dexterity to dexterity,
        Attribute.constitution to constitution,
        Attribute.intelligence to intelligence,
        Attribute.wisdom to wisdom,
        Attribute.charisma to charisma
    )

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
    data class Attribute(override val name: String, val shortName: String) : Proficiency.Subject {
        companion object {
            val strength = Attribute("Strength", "str")
            val dexterity = Attribute("Dexterity", "dex")
            val constitution = Attribute("Constitution", "con")
            val intelligence = Attribute("Intelligence", "int")
            val wisdom = Attribute("Wisdom", "wis")
            val charisma = Attribute("Charisma", "cha")

            fun modifier(value: Int): IntModifier = IntModifier { floor((value - 10).toFloat() / 2f).toInt() }
        }
    }
}

