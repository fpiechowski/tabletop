package tabletop.scene

import tabletop.Entity
import tabletop.Item
import tabletop.Usable
import tabletop.character.Character
import tabletop.geometry.Point

abstract class Token<T : Tokenizable>(
    val scene: Scene,
    val tokenizable: T,
    override var position: Point
) : Entity(), Usable.User, Usable.Targetable, Moveable

class ItemToken<T : Item<*, *>>(
    override val name: String,
    scene: Scene,
    tokenizable: T,
    position: Point
) : Token<T>(
    scene,
    tokenizable,
    position
)

class CharacterToken<T : Character>(
    override val name: String,
    scene: Scene,
    tokenizable: T,
    position: Point
) : Token<T>(
    scene,
    tokenizable,
    position
)
