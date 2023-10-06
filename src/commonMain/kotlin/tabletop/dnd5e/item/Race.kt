package tabletop.dnd5e.item

import tabletop.Item
import tabletop.dnd5e.DnD5eCharacter

class Race(
    override val name: String,
    val age: Age,
    val alignment: Set<DnD5eCharacter.Alignment>,
    val size: DnD5eCharacter.Size,
    val speed: Int
) : Item<DnD5eCharacter, Nothing>() {

    data class Age(
        val adulteryInYears: Int,
        val lifespanInYears: Int
    )
}
