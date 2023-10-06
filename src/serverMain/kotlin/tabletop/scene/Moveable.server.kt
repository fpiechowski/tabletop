package tabletop.scene

import tabletop.geometry.Point
import tabletop.persistence.Persistence

context (Persistence)
actual fun Moveable.move(destination: Point) {
    position = destination
    storageManager.store(this)
}
