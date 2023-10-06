package tabletop.scene

import kotlinx.uuid.UUID
import tabletop.Entity

class Scene(override val name: String) : Entity() {
    val tokens: MutableMap<UUID, Token<*>> = mutableMapOf()
}
