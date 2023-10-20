package tabletop.common

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID

@Serializable
abstract class Entity : Identifiable<UUID>

@Serializable
abstract class NamedEntity : Identifiable<UUID>, Named

interface Named {
    val name: String
}

interface Identifiable<T> {
    val id: T
}
