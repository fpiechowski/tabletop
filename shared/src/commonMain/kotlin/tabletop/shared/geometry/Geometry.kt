package tabletop.shared.geometry

import kotlinx.serialization.Serializable


@Serializable
data class Point(val x: Int, val y: Int) {
    operator fun plus(point: Point) = Point(x + point.x, y + point.y)
}
