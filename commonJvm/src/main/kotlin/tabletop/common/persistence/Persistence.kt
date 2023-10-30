package tabletop.common.persistence

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import kotlinx.serialization.Serializable
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.common.error.CommonError

abstract class Persistence<R>(
    private val storageManager: EmbeddedStorageManager
) {
    val persistenceRoot get() = storageManager.root() as R

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

    init {
        storageManager.storeRoot()
    }

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

