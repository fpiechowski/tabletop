package tabletop.common.error

import kotlinx.serialization.Serializable

@Serializable
abstract class CommonError {
    abstract val message: String?
    abstract val cause: CommonError?


    override fun toString(): String =
        """${this::class.qualifiedName}${message?.let { ": $it" }}${cause?.let { ", cause: $it" } ?: ""}""".trimMargin()

    fun CommonError.findThrowable(): ThrowableError? = when {
        this is ThrowableError -> this
        else -> cause?.findThrowable()
    }

    @Serializable
    class ThrowableError(
        override val message: String?,
        override val cause: CommonError?,
        val stackTrace: String
    ) : CommonError() {

        constructor(throwable: Throwable) :
                this(
                    "${throwable::class.qualifiedName}: ${throwable.message}",
                    throwable.cause?.let { ThrowableError(it) },
                    throwable.stackTraceToString()
                )
    }
}
