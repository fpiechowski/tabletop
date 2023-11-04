package tabletop.common.dnd5e.character

import tabletop.common.Named
import tabletop.common.dice.Dice

abstract class CharacterClass(
    val level: Int,
    val features: Set<Feature>,
    val hitDice: Dice,
    val proficiencies: Set<Proficiency<*>>
) : Named {
    abstract val advancement: Advancement<*>

    sealed interface Advancement<T> {
        val operation: Set<T>.(T) -> Unit

        interface FeatureAdvancement : Advancement<Feature> {
            val feature: Feature

            fun apply(characterClass: CharacterClass) = characterClass.features.apply { operation(feature) }
        }
    }
}
