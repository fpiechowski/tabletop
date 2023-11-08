package tabletop.common.dnd5e.item

import tabletop.common.dice.Dice
import java.util.*

class Weapon(
    override val name: String,
    val damage: Dice,
    val attackDamageBonus: Int = 0,
    override val id: UUID = UUID.randomUUID()
) : Item(), Equippable
