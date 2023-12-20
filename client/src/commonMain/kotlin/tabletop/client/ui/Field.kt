package tabletop.client.ui

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import tabletop.shared.error.CommonError
import tabletop.shared.error.UnsupportedSubtypeError
import kotlin.reflect.KClass

data class Field<T : Any>(
    val label: String,
    val type: KClass<T>,
) {
    @Suppress("UNCHECKED_CAST")
    fun fromString(string: String): Either<CommonError, T> = either {
        catch({
            when (type) {
                String::class -> string as T
                Int::class -> string.toInt() as T
                Long::class -> string.toLong() as T
                Float::class -> string.toFloat() as T
                Double::class -> string.toDouble() as T
                else -> raise(UnsupportedSubtypeError(type))
            }
        }) {
            raise(CommonError.ThrowableError(it))
        }
    }
} 
