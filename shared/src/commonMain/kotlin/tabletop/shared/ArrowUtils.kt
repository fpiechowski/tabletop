package tabletop.shared

import arrow.core.raise.catch
import arrow.core.raise.either
import tabletop.shared.error.CommonError
import kotlin.text.toInt as unsafeToInt

fun String.toInt() = either {
    catch({ unsafeToInt() }) {
        raise(CommonError.ThrowableError(it))
    }
}