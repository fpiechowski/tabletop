package tabletop.common.error

import kotlinx.serialization.Serializable

@Serializable
actual abstract class CommonError {
    actual abstract val message: String?
    actual abstract val cause: CommonError?

    actual override fun toString() =
        """${this::class}${message?.let { ": $it" }}${cause?.let { ", cause: $it" } ?: ""}""".trimMargin()

    actual fun CommonError.findThrowable(): ThrowableError? = when {
        this is ThrowableError -> this
        else -> cause?.findThrowable()
    }

    @Serializable
    actual class ThrowableError(
        actual override val message: String?,
        actual override val cause: CommonError?,
        actual val stackTrace: String
    ) : CommonError() {
        actual constructor(throwable: Throwable) :
                this(
                    "${throwable::class}: ${throwable.message}",
                    throwable.cause?.let { ThrowableError(it) },
                    throwable.stackTraceToString()
                )
    }
}