package tabletop.shared.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.dice.Dice

class Weapon(
    override val name: String,
    val damage: Dice,
    val attackDamageBonus: Int = 0,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Item(), Equippable
