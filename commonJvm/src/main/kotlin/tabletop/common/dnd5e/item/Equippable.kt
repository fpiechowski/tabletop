package tabletop.common.dnd5e.item

import tabletop.common.dnd5e.character.Character

interface Equippable {
    fun Character.equip(): Character = TODO()
    fun Character.unequip(): Unit = TODO()
}
