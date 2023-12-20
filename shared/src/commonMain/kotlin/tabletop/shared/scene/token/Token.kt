package tabletop.shared.scene.token

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity
import tabletop.shared.Usable
import tabletop.shared.geometry.Point
import tabletop.shared.scene.Moveable

@Serializable
class Token<T : Tokenizable>(
    override val name: String,
    override var position: Point,
    val gameId: UUID,
    val sceneId: UUID,
    val tokenizableId: UUID,
    val imageFilePath: String,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID()
) : Entity(), Usable.User, Usable.Target, Moveable
