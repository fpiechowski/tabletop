package tabletop.common.error

import java.io.Serializable
import java.util.*
import kotlin.reflect.KClass

abstract class CommonError : Serializable {
    abstract val message: String?
    abstract val cause: CommonError?

    companion object {
        private const val serialVersionUID = 1L
    }


    override fun toString(): String =
        """${this::class.qualifiedName}${message?.let { ": $it" }}${cause?.let { ", cause: $it" } ?: ""}""".trimMargin()

    fun CommonError.findThrowable(): ThrowableError? = when {
        this is ThrowableError -> this
        else -> cause?.findThrowable()
    }

    class ThrowableError(
        override val message: String?,
        override val cause: CommonError?,
        val stackTrace: String
    ) : CommonError(), Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }

        constructor(throwable: Throwable) :
                this(
                    "${throwable::class.qualifiedName}: ${throwable.message}",
                    throwable.cause?.let { ThrowableError(it) },
                    throwable.stackTraceToString()
                )
    }
}

class UnsupportedSubtypeError<T : Any>(klass: KClass<T>) : CommonError(), Serializable {
    override val message: String = "Unsupported subtype of $klass"
    override val cause: CommonError? = null

    companion object {
        private const val serialVersionUID = 1L
    }

}

class NotFoundError<T : Any>(klass: KClass<T>? = null, id: UUID) : CommonError(), Serializable {
    override val message: String = "${klass?.qualifiedName ?: "Entity"} with ID $id not found"
    override val cause: CommonError? = null

    companion object {
        private const val serialVersionUID = 1L
    }

}