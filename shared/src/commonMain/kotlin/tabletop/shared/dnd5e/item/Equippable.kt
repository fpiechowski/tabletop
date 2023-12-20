package tabletop.shared.dnd5e.item

import tabletop.shared.dnd5e.character.Character

interface Equippable {
    fun Character.equip(): Character = TODO()
    fun Character.unequip(): Unit = TODO()
}
