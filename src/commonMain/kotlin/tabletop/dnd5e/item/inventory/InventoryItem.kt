package tabletop.dnd5e.item.inventory

import tabletop.Item
import tabletop.dnd5e.DnD5eCharacter
import tabletop.dnd5e.Money
import tabletop.dnd5e.item.Feature
import tabletop.dnd5e.item.Spell
import tabletop.scene.Token

abstract class InventoryItem(
    val spells: Set<Spell> = setOf(),
    val features: Set<Feature> = setOf(),
    val price: Money = Money.zero,
    val weight: Int = 0,
) : Item<DnD5eCharacter, Token<DnD5eCharacter>>()
