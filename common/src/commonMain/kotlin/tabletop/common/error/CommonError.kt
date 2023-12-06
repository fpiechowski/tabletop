package tabletop.common.error


import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlin.reflect.KClass

@Serializable
expect abstract class CommonError() {
    abstract val message: String?
    abstract val cause: CommonError?

    override fun toString(): String

    fun CommonError.findThrowable(): ThrowableError?

    class ThrowableError(throwable: Throwable) : CommonError {
        override val message: String?
        override val cause: CommonError?
        val stackTrace: String
    }
}

 class UnsupportedSubtypeError<T : Any>(klass: KClass<T>) : CommonError() {
    override val message: String = "Unsupported subtype of $klass"
    override val cause: CommonError? = null
}

class NotFoundError<T: Any>(kind: KClass<T>, id: UUID) : CommonError() {
    override val message: String = "$kind with ID $id not found"
    override val cause: CommonError? = null
}

class InvalidSubtypeError<T : Any, T2: T>(klass: KClass<T>, klass2: KClass<T2>) : CommonError() {
    override val message: String = "Invalid subtype of $klass: $klass2"
    override val cause: CommonError? = null
}