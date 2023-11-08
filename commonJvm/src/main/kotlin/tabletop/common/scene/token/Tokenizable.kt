package tabletop.common.scene.token

import tabletop.common.Identifiable
import tabletop.common.geometry.Point
import tabletop.common.scene.Scene
import java.util.*

interface Tokenizable : Identifiable<UUID> {
    val tokenImageFilePath: String

    fun tokenize(scene: Scene, position: Point): Token<*>
}

