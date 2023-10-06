package tabletop.dnd5e.item

import tabletop.Item
import tabletop.dnd5e.DnD5eCharacter
import tabletop.scene.Token

abstract class Feature : Item<DnD5eCharacter, Token<DnD5eCharacter>>()
