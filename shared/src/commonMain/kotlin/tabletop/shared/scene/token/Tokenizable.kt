package tabletop.shared.scene.token

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.shared.entity.Entity
import tabletop.shared.entity.Identifiable
import tabletop.shared.geometry.Point
import tabletop.shared.scene.Scene

interface Tokenizable : Identifiable<UUID> {
    val tokenImageFilePath: String

    fun tokenize(scene: Scene, position: Point): Token<*>
}

@Serializable
abstract class TokenizableEntity : Entity(), Tokenizable