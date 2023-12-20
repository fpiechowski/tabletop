package tabletop.shared.entity

import kotlinx.uuid.UUID


abstract class Entity : Identifiable<UUID>, Named {
    abstract val image: String?
}


interface Named {
    val name: String
}

interface Identifiable<T> {
    val id: T
}
