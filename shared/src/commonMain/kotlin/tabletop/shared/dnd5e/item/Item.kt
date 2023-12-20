package tabletop.shared.dnd5e.item

import kotlinx.serialization.Serializable
import tabletop.shared.entity.Entity
import tabletop.shared.dnd5e.Money
import tabletop.shared.dnd5e.character.Feature
import tabletop.shared.dnd5e.magic.Spell

@Serializable
abstract class Item(
    val spells: Set<Spell> = setOf(),
    val features: Set<Feature> = setOf(),
    val price: Money = Money.zero,
    val weight: Int = 0,
) : Entity()
