package tabletop.common.dnd5e.item.inventory

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Game
import tabletop.common.dice.Dice
import tabletop.common.dnd5e.DnD5eCharacter
import tabletop.common.scene.Token

class Weapon(
    override val name: String,
    val damage: Dice,
    val attackDamageBonus: Int = 0,
    override val id: UUID = UUID.generateUUID()
) : InventoryItem(), Equippable {
    override fun use(game: Game, user: DnD5eCharacter, targets: Set<Token<DnD5eCharacter>>) {
        super.use(game, user, targets)

        user.attack(this, targets)
    }

    override fun DnD5eCharacter.equip(): DnD5eCharacter {
        TODO("Not yet implemented")
    }

    override fun DnD5eCharacter.unequip(): DnD5eCharacter {
        TODO("Not yet implemented")
    }
}
