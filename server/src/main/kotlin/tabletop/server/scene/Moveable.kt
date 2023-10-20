package tabletop.server.scene

import tabletop.common.geometry.Point
import tabletop.common.scene.Moveable
import tabletop.server.persistence.Persistence

fun Moveable.move(destination: Point) {
    with(Persistence) {
        position = destination
        storageManager.store(this)
    }
}
