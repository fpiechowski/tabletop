package tabletop.common.scene

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.Usable
import tabletop.common.geometry.Point
import tabletop.common.rpg.character.Character
import tabletop.common.rpg.item.Item

@Serializable
abstract class Token<T : Tokenizable>(
    val scene: Scene,
    val tokenizable: T,
    override var position: Point
) : NamedEntity(), Usable.User, Usable.Targetable, Moveable

class ItemToken<T : Item<*, *>>(
    override val name: String,
    scene: Scene,
    tokenizable: T,
    position: Point,
    override val id: UUID = UUID.generateUUID()
) : Token<T>(
    scene,
    tokenizable,
    position
)

class CharacterToken<T : Character>(
    override val name: String,
    scene: Scene,
    tokenizable: T,
    position: Point,
    override val id: UUID = UUID.generateUUID()
) : Token<T>(
    scene,
    tokenizable,
    position
)
