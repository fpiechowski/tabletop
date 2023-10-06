package tabletop.dnd5e.item.inventory

import tabletop.dnd5e.DnD5eCharacter

interface Equippable {
    fun DnD5eCharacter.equip(): DnD5eCharacter = copy(equipped = equipped + this@Equippable)
    fun DnD5eCharacter.unequip() = copy(equipped = equipped - this@Equippable)
}