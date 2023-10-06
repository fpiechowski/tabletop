package tabletop

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID

@Serializable
abstract class Entity(override val id: UUID = UUID.generateUUID()) : Named, Identifiable<UUID>

interface Named {
    val name: String
}

interface Identifiable<T> {
    val id: T
}
