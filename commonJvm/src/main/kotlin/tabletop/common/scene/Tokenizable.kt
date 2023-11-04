package tabletop.common.scene

import tabletop.common.geometry.Point

interface Tokenizable {
    val tokenImageFilePath: String

    fun tokenize(scene: Scene, position: Point): Token<*>
}
