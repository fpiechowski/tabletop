package tabletop.common

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.optics.Lens
import arrow.optics.PLens
import kotlinx.uuid.UUID
import tabletop.common.entity.Identifiable
import tabletop.common.error.CommonError
import tabletop.common.error.NotFoundError


inline fun <reified T : Identifiable<UUID>> idLens(id: UUID): Either<CommonError, Lens<Map<UUID, T>, T>> =
    either {
        Lens(
            get = { iterable -> ensureNotNull(iterable[id]) { NotFoundError(T::class, id) } },
            set = { map, t ->
                ensureNotNull(t) { CommonError.ThrowableError(NullPointerException("Can't use idLens to add null element")) }
                map + (t.id to t)
            }
        )
    }