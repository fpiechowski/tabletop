package tabletop.shared.dnd5e.skill

import kotlinx.serialization.Serializable
import tabletop.shared.dnd5e.character.Character
import tabletop.shared.dnd5e.character.Proficiency

@Serializable
data class Skill (override val name: String, val attribute: Character.Attribute) : Proficiency.Subject {

    companion object {
        val athletics = Skill("Athletics", Character.Attribute.Strength)
        val acrobatics = Skill("Acrobatics", Character.Attribute.Dexterity)
        val sleightOfHand = Skill("Sleight of Hand", Character.Attribute.Dexterity)
        val stealth = Skill("Stealth", Character.Attribute.Dexterity)
        val arcana = Skill("Arcana", Character.Attribute.Intelligence)
        val history = Skill("History", Character.Attribute.Intelligence)
        val investigation = Skill("Investigation", Character.Attribute.Intelligence)
        val nature = Skill("Nature", Character.Attribute.Intelligence)
        val religion = Skill("Religion", Character.Attribute.Intelligence)
        val AnimalHandling = Skill("Animal Handling", Character.Attribute.Wisdom)
        val insight = Skill("Insight", Character.Attribute.Wisdom)
        val medicine = Skill("Medicine", Character.Attribute.Wisdom)
        val perception = Skill("Perception", Character.Attribute.Wisdom)
        val survival = Skill("Survival", Character.Attribute.Wisdom)
        val deception = Skill("Deception", Character.Attribute.Charisma)
        val intimidation = Skill("Deception", Character.Attribute.Charisma)
        val performance = Skill("Performance", Character.Attribute.Charisma)
        val persuation = Skill("Persuasion", Character.Attribute.Charisma)

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

