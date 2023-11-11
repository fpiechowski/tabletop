package tabletop.common

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler

fun <T, E : CommonError> Flow<Either<E, T>>.handleErrors(
    errorHandler: ErrorHandler<CommonError>
): Flow<T> =
    transform { either ->
        either.fold({ with(errorHandler) { it.handle() } }) {
            emit(it)
        }
    }