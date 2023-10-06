package tabletop.character

import tabletop.Entity
import tabletop.Game
import tabletop.scene.Tokenizable

open class Character(override val name: String) : Entity(), Tokenizable, Game.Chat.Speaker
