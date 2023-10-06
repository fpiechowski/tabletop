package tabletop

open class CommonError(
    open val message: String? = null,
    open val cause: CommonError? = null
) {
    class ThrowableException(
        val throwable: Throwable
    ) : CommonError(throwable.message)

    companion object {
        fun from(throwable: Throwable): CommonError =
            CommonError(throwable.message, ThrowableException(throwable))
    }
}
