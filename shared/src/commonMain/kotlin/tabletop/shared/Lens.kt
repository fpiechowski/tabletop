package tabletop.shared

import arrow.core.getOrNone
import arrow.optics.Optional
import kotlinx.uuid.UUID
import tabletop.shared.entity.Identifiable

inline fun <reified T : Identifiable<UUID>> idOptional(id: UUID): Optional<Map<UUID, T>, T> =
    Optional(
        getOption = { iterable -> iterable.getOrNone(id) },
        set = { map, t ->
            map + (t.id to t)
        }
    )
