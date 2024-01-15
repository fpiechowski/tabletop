package tabletop.shared

import kotlinx.uuid.UUID
import tabletop.shared.entity.Identifiable


operator fun <T : Identifiable<UUID>> Map<UUID, T>.plus(identifiable: T): Map<UUID, T> =
    if (this.isEmpty()) mapOf(identifiable.id to identifiable) else LinkedHashMap(this).apply {
        put(
            identifiable.id,
            identifiable
        )
    }