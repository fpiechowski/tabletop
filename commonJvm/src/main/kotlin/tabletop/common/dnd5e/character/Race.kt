package tabletop.common.dnd5e.character

import tabletop.common.Named

abstract class Race(
    override val name: String,
    val age: Age,
    val alignment: Set<DnD5eCharacter.Alignment>,
    val size: DnD5eCharacter.Size,
    val speed: Int
) : Named {

    data class Age(
        val adulteryInYears: Int,
        val lifespanInYears: Int
    )
}
