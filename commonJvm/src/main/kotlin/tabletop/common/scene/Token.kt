package tabletop.common.scene

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.Usable
import tabletop.common.geometry.Point

@Serializable
class Token<T : Tokenizable>(
    override val name: String,
    override var position: Point,
    val scene: Scene,
    val tokenizable: T,
    override val id: UUID = UUID.generateUUID()
) : NamedEntity(), Usable.User, Usable.Targetable, Moveable
