package tabletop.shared.dnd5e.character

import kotlinx.serialization.Serializable
import tabletop.shared.dice.Dice
import tabletop.shared.dice.d
import tabletop.shared.entity.Named

@Serializable
class CharacterClass(
    override val name: String,
    val hitDice: Dice,
    val proficiencies: Set<Proficiency<*>> = setOf(),
    val advancements: Advancements = Advancements()
) : Named {

    @Serializable
    class Advancements : Map<Int, Advancements.Advancement> by mapOf() {
        interface Advancement {
            val proficiencyBonusChange: (Int) -> Int
            val features: Set<Feature>
        }
    }

    companion object {
        val figter = CharacterClass(
            "Fighter",
            1 d 10,
            setOf(

            ),
        )
    }
}