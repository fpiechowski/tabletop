package tabletop.common.dnd5e.item

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dice.Dice
import tabletop.common.dnd5e.character.DnD5eCharacter

class Weapon(
    override val name: String,
    val damage: Dice,
    val attackDamageBonus: Int = 0,
    override val id: UUID = UUID.generateUUID()
) : Item(), Equippable {

    override fun DnD5eCharacter.equip(): DnD5eCharacter {
        TODO("Not yet implemented")
    }

    override fun DnD5eCharacter.unequip(): DnD5eCharacter {
        TODO("Not yet implemented")
    }
}
