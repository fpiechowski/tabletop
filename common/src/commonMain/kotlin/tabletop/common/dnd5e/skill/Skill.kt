package tabletop.common.dnd5e.skill

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import tabletop.common.dnd5e.character.Character
import tabletop.common.dnd5e.character.Proficiency

@Serializable
data class Skill (override val name: String, val attribute: Character.Attribute) : Proficiency.Subject {

    companion object {
        val athletics = Skill("Athletics", Character.Attribute.strength)
        val acrobatics = Skill("Acrobatics", Character.Attribute.dexterity)
        val sleightOfHand = Skill("Sleight of Hand", Character.Attribute.dexterity)
        val stealth = Skill("Stealth", Character.Attribute.dexterity)
        val arcana = Skill("Arcana", Character.Attribute.intelligence)
        val history = Skill("History", Character.Attribute.intelligence)
        val investigation = Skill("Investigation", Character.Attribute.intelligence)
        val nature = Skill("Nature", Character.Attribute.intelligence)
        val religion = Skill("Religion", Character.Attribute.intelligence)
        val AnimalHandling = Skill("Animal Handling", Character.Attribute.wisdom)
        val insight = Skill("Insight", Character.Attribute.wisdom)
        val medicine = Skill("Medicine", Character.Attribute.wisdom)
        val perception = Skill("Perception", Character.Attribute.wisdom)
        val survival = Skill("Survival", Character.Attribute.wisdom)
        val deception = Skill("Deception", Character.Attribute.charisma)
        val intimidation = Skill("Deception", Character.Attribute.charisma)
        val performance = Skill("Performance", Character.Attribute.charisma)
        val persuation = Skill("Persuasion", Character.Attribute.charisma)

        val all = setOf(
            athletics,
            acrobatics,
            sleightOfHand,
            stealth,
            arcana,
            history,
            investigation,
            nature,
            religion,
            AnimalHandling,
            insight,
            medicine,
            perception,
            survival,
            deception,
            intimidation,
            performance,
            persuation
        )
    }
}

