package tabletop.server.persistence

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.optics.optics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import org.eclipse.store.storage.embedded.types.EmbeddedStorage
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager
import tabletop.shared.auth.Credentials
import tabletop.shared.error.CommonError
import tabletop.shared.game.Game
import tabletop.shared.user.User
import kotlin.coroutines.CoroutineContext

class Persistence(
    private val storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root()),
) : CoroutineScope {
    init {
        storageManager.storeRoot()
    }

    
    data class Root(
        val games: Map<UUID, Game<*>> = mapOf(),
        val users: Map<UUID, User> = mapOf(),
        val credentials: Map<User, Credentials.UsernamePassword> = mutableMapOf(),
    ) {
        companion object
    }

    @Serializable
    data class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    fun <T> T.persist(): Either<Error, Unit> =
        either {
            catch({
                when (this@persist) {
                    is Root -> storageManager.run {
                        setRoot(this@persist)
                        storeRoot()
                    }

                    else -> storageManager.store(this@persist)
                }
            }) {
                raise(Error("Can't store entity ${this@persist}", CommonError.ThrowableError(it)))
            }
        }


    fun <T : Any> retrieve(get: Root.() -> T?): Either<Error, T> =
        either {
            catch({
                persistenceRoot.let(get) ?: raise(
                    Error(
                        "Can't retrieve entity",
                        CommonError.ThrowableError(NullPointerException())
                    )
                )
            }) {
                raise(Error("Can't retrieve entity", CommonError.ThrowableError(it)))
            }
        }

    val persistenceRoot: Root get() = storageManager.root() as Root

    override val coroutineContext: CoroutineContext = Dispatchers.IO
}

