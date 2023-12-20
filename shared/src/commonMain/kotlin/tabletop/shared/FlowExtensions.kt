package tabletop.shared

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import tabletop.shared.error.CommonError
import tabletop.shared.error.ErrorHandler

fun <T, E : CommonError> Flow<Either<E, T>>.handleErrors(
    errorHandler: ErrorHandler<CommonError>
): Flow<T> =
    transform { either ->
        either.fold({ with(errorHandler) { it.handle() } }) {
            emit(it)
        }
    }