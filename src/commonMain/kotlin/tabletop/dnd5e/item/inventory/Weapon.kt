package tabletop.dnd5e.item.inventory

import tabletop.Game
import tabletop.dice.Dice
import tabletop.dnd5e.DnD5eCharacter
import tabletop.scene.Token

class Weapon(
    override val name: String,
    val damage: Dice,
    val attackDamageBonus: Int = 0
) : InventoryItem(), Equippable {
    override fun use(game: Game<*>, user: DnD5eCharacter, targets: Set<Token<DnD5eCharacter>>) {
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
