package tabletop.common.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dnd5e.DnD5eCharacter
import tabletop.common.rpg.item.Item

class Race(
    override val name: String,
    val age: Age,
    val alignment: Set<DnD5eCharacter.Alignment>,
    val size: DnD5eCharacter.Size,
    val speed: Int,
    override val id: UUID = UUID.generateUUID()
) : Item<DnD5eCharacter, Nothing>() {

    data class Age(
        val adulteryInYears: Int,
        val lifespanInYears: Int
    )
}
