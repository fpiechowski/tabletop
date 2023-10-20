package tabletop.common

import arrow.core.Either
import arrow.core.Option
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import tabletop.common.error.CommonError

fun <T, E : CommonError> Flow<Either<E, T>>.transformFold(
    ifLeft: suspend (CommonError) -> Unit
): Flow<T> =
    transform { either ->
        either.fold({ ifLeft(it) }) {
            emit(it)
        }
    }

fun <T> Flow<Option<T>>.transformFold(ifNone: suspend () -> Unit): Flow<T> =
    transform { option ->
        option.fold({ ifNone() }) { emit(it) }
    }
