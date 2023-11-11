package tabletop.common.error

actual abstract class CommonError {
    actual abstract val message: String?
    actual abstract val cause: CommonError?
    actual fun toString(): String {
        TODO("Not yet implemented")
    }

    actual fun CommonError.findThrowable(): ThrowableError? {
        TODO("Not yet implemented")
    }

    actual class ThrowableError(
        actual override val message: String?,
        override val cause: CommonError?,
        val stackTrace: String
    ) : CommonError() {
        actual constructor(throwable: Throwable) : this(, ,) {
            TODO("Not yet implemented")
        }
    }
}