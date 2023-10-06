package tabletop.scene

import tabletop.geometry.Point

interface Moveable {
    var position: Point
}

expect fun Moveable.move(destination: Point)
