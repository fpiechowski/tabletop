package tabletop.server.persistence

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import one.microstream.storage.embedded.types.EmbeddedStorage
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.error.CommonError
import tabletop.common.user.User


class Persistence(private val storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root)) {
    val persistenceRoot = Root


    fun <T> T.persist(): Either<Error, Unit> =
        either {
            catch({
                storageManager.store(this@persist)
            }) {
                raise(Error("Can't store entity ${this@persist}", CommonError.ThrowableError(it)))
            }
        }


    fun <T> retrieve(get: Root.() -> T): Either<Error, T> =
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

    object Root {
        val games: MutableMap<UUID, Game> = mutableMapOf()
        val users: MutableMap<UUID, User> = mutableMapOf()
        val credentials: MutableMap<User, Credentials<*, *>> = mutableMapOf()
    }

    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}

