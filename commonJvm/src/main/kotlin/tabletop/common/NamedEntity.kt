package tabletop.common

import java.io.Serializable
import java.util.*

abstract class Entity : Identifiable<UUID>, Serializable

abstract class NamedEntity : Entity(), Named, Serializable

interface Named {
    val name: String
}

interface Identifiable<T> {
    val id: T
}
