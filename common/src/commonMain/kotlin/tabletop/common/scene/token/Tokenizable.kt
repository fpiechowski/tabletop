package tabletop.common.scene.token

import kotlinx.uuid.UUID
import tabletop.common.entity.Identifiable
import tabletop.common.geometry.Point
import tabletop.common.scene.Scene

interface Tokenizable : Identifiable<UUID> {
    val tokenImageFilePath: String

    fun tokenize(scene: Scene, position: Point): Token<*>
}

