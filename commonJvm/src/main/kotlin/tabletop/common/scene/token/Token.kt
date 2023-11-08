package tabletop.common.scene.token

import tabletop.common.NamedEntity
import tabletop.common.Usable
import tabletop.common.geometry.Point
import tabletop.common.scene.Moveable
import tabletop.common.scene.Scene
import java.io.Serializable
import java.util.*

class Token<T : Tokenizable>(
    override val name: String,
    override var position: Point,
    val scene: Scene,
    val tokenizable: T,
    val imageFilePath: String,
    override val id: UUID = UUID.randomUUID()
) : NamedEntity(), Usable.User, Usable.Target, Moveable, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }
}

