package tabletop.common.scene.token

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.Usable
import tabletop.common.geometry.Point
import tabletop.common.scene.Moveable
import tabletop.common.scene.Scene

class Token<T : Tokenizable>(
    override val name: String,
    override var position: Point,
    val scene: Scene,
    val tokenizable: T,
    val imageFilePath: String,
    override val id: UUID = UUID.generateUUID()
) : NamedEntity(), Usable.User, Usable.Target, Moveable

