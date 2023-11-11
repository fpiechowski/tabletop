package tabletop.common.error


import kotlinx.uuid.UUID
import kotlin.reflect.KClass

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

class NotFoundError<T : Any>(klass: KClass<T>? = null, id: UUID) : CommonError() {
    override val message: String = "${klass?.qualifiedName ?: "Entity"} with ID $id not found"
    override val cause: CommonError? = null
}