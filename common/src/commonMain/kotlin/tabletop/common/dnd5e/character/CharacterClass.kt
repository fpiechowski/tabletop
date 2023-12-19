package tabletop.common.dnd5e.character

import kotlinx.serialization.Serializable
import tabletop.common.dice.Dice
import tabletop.common.dice.d
import tabletop.common.entity.Named

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