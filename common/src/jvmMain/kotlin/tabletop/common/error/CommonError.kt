package tabletop.common.error

actual abstract class CommonError {
    actual abstract val message: String?
    actual abstract val cause: CommonError?

    actual override fun toString() =
        """${this::class.qualifiedName}${message?.let { ": $it" }}${cause?.let { ", cause: $it" } ?: ""}""".trimMargin()

    actual fun CommonError.findThrowable(): ThrowableError? = when {
        this is ThrowableError -> this
        else -> cause?.findThrowable()
    }


    actual class ThrowableError(
        actual override val message: String?,
        actual override val cause: CommonError?,
        actual val stackTrace: String
    ) : CommonError() {

        actual constructor(throwable: Throwable) :
                this(
                    "${throwable::class.qualifiedName}: ${throwable.message}",
                    throwable.cause?.let { ThrowableError(it) },
                    throwable.stackTraceToString()
                )
    }


}