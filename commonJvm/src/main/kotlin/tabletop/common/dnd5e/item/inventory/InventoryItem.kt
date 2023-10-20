package tabletop.common.dnd5e.item.inventory

import tabletop.common.dnd5e.DnD5eCharacter
import tabletop.common.dnd5e.Money
import tabletop.common.dnd5e.item.Feature
import tabletop.common.dnd5e.item.Spell
import tabletop.common.rpg.item.Item
import tabletop.common.scene.Token

abstract class InventoryItem(
    val spells: Set<Spell> = setOf(),
    val features: Set<Feature> = setOf(),
    val price: Money = Money.zero,
    val weight: Int = 0,
) : Item<DnD5eCharacter, Token<DnD5eCharacter>>()
