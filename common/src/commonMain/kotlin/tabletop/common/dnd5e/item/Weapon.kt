package tabletop.common.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dice.Dice

class Weapon(
    override val name: String,
    val damage: Dice,
    val attackDamageBonus: Int = 0,
    override val id: UUID = UUID.generateUUID()
) : Item(), Equippable
