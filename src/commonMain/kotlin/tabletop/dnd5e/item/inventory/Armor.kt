package tabletop.dnd5e.item.inventory

import tabletop.dnd5e.item.Proficiency

class Armor(
    override val name: String,
    val acBonus: Int
) : InventoryItem(), Equippable {

    enum class Type : Proficiency.Subject {
        Light, Medium, Heavy, Shield
    }
}
