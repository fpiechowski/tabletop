package tabletop.client.entity

import arrow.core.raise.either
import kotlinx.uuid.UUID
import tabletop.shared.entity.Identifiable
import tabletop.shared.error.NotFoundError

inline fun <reified T : Identifiable<UUID>> Iterable<T>.get(id: UUID) = either {
    find { it.id == id }
        ?: raise(NotFoundError(T::class, id))
}