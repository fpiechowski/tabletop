package tabletop.common.persistence

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.common.error.CommonError

abstract class Persistence<R> {
    abstract val storageManager: EmbeddedStorageManager
    abstract val persistenceRoot: R

    fun <T> T.persist(): Either<Error, Unit> =
        either {
            catch({
                storageManager.store(this@persist)
            }) {
                raise(Error("Can't store entity ${this@persist}", CommonError.ThrowableError(it)))
            }
        }


    fun <T> retrieve(get: R.() -> T): Either<Error, T> =
        either {
            catch({
                persistenceRoot.let(get)
            }) {
                raise(Error("Can't retrieve entity", CommonError.ThrowableError(it)))
            }
        }

    class Error(override val message: String?, override val cause: CommonError?) : CommonError(), java.io.Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}

