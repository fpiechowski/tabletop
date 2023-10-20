package tabletop.common.dnd5e.item.inventory

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dnd5e.item.Proficiency

class Armor(
    override val name: String,
    val acBonus: Int,
    override val id: UUID = UUID.generateUUID()
) : InventoryItem(), Equippable {

    enum class Type : Proficiency.Subject {
        Light, Medium, Heavy, Shield
    }
}
