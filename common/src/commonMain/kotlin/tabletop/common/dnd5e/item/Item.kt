package tabletop.common.dnd5e.item

import tabletop.common.entity.Entity
import tabletop.common.dnd5e.Money
import tabletop.common.dnd5e.character.Feature
import tabletop.common.dnd5e.magic.Spell

abstract class Item(
    val spells: Set<Spell> = setOf(),
    val features: Set<Feature> = setOf(),
    val price: Money = Money.zero,
    val weight: Int = 0,
) : Entity()
