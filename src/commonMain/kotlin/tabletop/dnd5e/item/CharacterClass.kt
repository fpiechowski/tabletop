package tabletop.dnd5e.item

import tabletop.Item
import tabletop.dice.Dice
import tabletop.dnd5e.DnD5eCharacter

abstract class CharacterClass(
    val level: Int,
    val features: Set<Feature>,
    val hitDice: Dice,
    val proficiencies: Set<Proficiency<*>>
) : Item<DnD5eCharacter, DnD5eCharacter>() {
    abstract val advancement: Advancement<*>

    sealed interface Advancement<T> {
        val operation: Set<T>.(T) -> Unit

        interface FeatureAdvancement : Advancement<Feature> {
            val feature: Feature

            fun apply(characterClass: CharacterClass) = characterClass.features.apply { operation(feature) }
        }
    }
}
