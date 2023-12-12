package tabletop.common.error


import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.common.entity.Entity
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

@Serializable
data class UnsupportedSubtypeError(
    override val message: String,
    override val cause: CommonError? = null
) : CommonError() {

    companion object {
        operator fun <T : Any> invoke(klass: KClass<T>) =
            UnsupportedSubtypeError(message = "Unsupported subtype of $klass")
    }
}


@Serializable
open class NotFoundError(
    override val message: String,
    override val cause: CommonError? = null
) : CommonError() {

    companion object {
        operator fun <T : Any> invoke(kind: KClass<T>, id: UUID) = NotFoundError("$kind with ID $id not found")
    }
}

@Serializable
data class InvalidSubtypeError(
    override val message: String,
    override val cause: CommonError? = null
) : CommonError() {


    companion object {
        operator fun <T : Any, T2 : T> invoke(klass: KClass<T>, klass2: KClass<T2>) =
            InvalidSubtypeError("Invalid subtype of $klass: $klass2")
    }
}