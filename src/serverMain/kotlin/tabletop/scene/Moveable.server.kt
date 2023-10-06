package tabletop.scene

import tabletop.geometry.Point
import tabletop.persistence.Persistence

actual fun Moveable.move(destination: Point) {
    with(Persistence) {
        position = destination
        storageManager.store(this)
    }
}
