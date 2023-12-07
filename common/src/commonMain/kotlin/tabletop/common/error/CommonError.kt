package tabletop.common.error


import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlin.reflect.KClass


@Serializable
 abstract class CommonError {
     abstract val message: String?
     abstract val cause: CommonError?

     override fun toString() =
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