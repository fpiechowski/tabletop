package tabletop.common.dnd5e.item

import tabletop.common.dnd5e.character.Proficiency
import java.util.*

class Armor(
    override val name: String,
    val acBonus: Int,
    override val id: UUID = UUID.randomUUID()
) : Item(), Equippable {

    enum class Type : Proficiency.Subject {
        Light, Medium, Heavy, Shield
    }
}
