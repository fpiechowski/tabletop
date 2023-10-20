package tabletop.common.dnd5e.item

import tabletop.common.dnd5e.DnD5eCharacter
import tabletop.common.rpg.item.Item
import tabletop.common.scene.Token

abstract class Feature : Item<DnD5eCharacter, Token<DnD5eCharacter>>()
