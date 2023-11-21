package tabletop.client

import arrow.core.Either
import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import dev.fritz2.core.lensOf
import kotlinx.uuid.UUID
import tabletop.client.state.State
import tabletop.common.entity.Identifiable
import tabletop.common.error.CommonError

fun <T> Store<T>.update(block: (T) -> T) = update(block(current))

fun <D : Any> Store<D?>.ensureNotNull(ifNull: () -> CommonError): Either<CommonError, Store<D>> = either {
    map(lensOf("ensureNotNull", {
        ensureNotNull(it, ifNull)
    }, { _, new -> new }))
}



@Suppress("UNCHECKED_CAST")
fun <I : Iterable<T>, T : Identifiable<UUID>> idLensOf(id: UUID): Lens<I, T?> = lensOf(
    id = "idLens",
    {
        it.find { it.id == id }
    },
    { set, scene ->
        (scene?.let { set + scene } ?: set) as I
    }
)

fun <T : Any?> Raise<CommonError>.notNullableEnsuredLens(ifNull: () -> CommonError): Lens<T?, T> =
    lensOf(
        id = "notNullableEnsured",
        {
            ensureNotNull(it, ifNull)
        },
        { _, new -> new }
    )

fun <S, T> arrow.optics.Lens<S, T>.toFritz2(id: String) =
    lensOf<S, T>(
        id,
        { get(it) },
        { s, b -> set(s, b) }
    )