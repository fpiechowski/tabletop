package tabletop.common

import kotlinx.uuid.UUID


abstract class Entity : Identifiable<UUID>

abstract class NamedEntity : Entity(), Named

interface Named {
    val name: String
}

interface Identifiable<T> {
    val id: T
}
