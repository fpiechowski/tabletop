package tabletop.server.persistence

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.optics.Lens
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.uuid.UUID
import org.eclipse.store.storage.embedded.types.EmbeddedStorage
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager
import tabletop.shared.auth.Credentials
import tabletop.shared.error.CommonError
import tabletop.shared.game.Game
import tabletop.shared.persistence.Persistence
import tabletop.shared.user.User
import kotlin.coroutines.CoroutineContext

typealias Users = Map<UUID, User>
typealias Games = Map<UUID, Game<*>>
typealias Credentials = Map<UUID, Credentials.UsernamePassword>

class Persistence(
    private val storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root()),
) : Persistence, CoroutineScope {
    init {
        storageManager.storeRoot()
    }

    data class Root(
        val games: Map<UUID, Game<*>> = mapOf(),
        val users: Map<UUID, User> = mapOf(),
        val credentials: Map<User, Credentials.UsernamePassword> = mutableMapOf(),
    ) {
        companion object {
            val users: Lens<Root, Users> = Lens(
                get = { it.users },
                set = { root, users -> root.copy(users = users) }
            )

            val games: Lens<Root, Games> = Lens(
                get = { it.games },
                set = { root, games -> root.copy(games = games) }
            )
        }
    }

    fun <T> T.persist(): Either<Persistence.Error, Unit> =
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
                raise(Persistence.Error("Can't store entity ${this@persist}", CommonError.ThrowableError(it)))
            }
        }


    inline fun <reified T : Any> retrieve(get: Root.() -> T?): Either<Persistence.Error, T> =
        either {
            catch({
                persistenceRoot.let(get) ?: raise(
                    Persistence.Error(
                        "Such instance of ${T::class} does not exist",
                        CommonError.ThrowableError(NullPointerException())
                    )
                )
            }) {
                raise(Persistence.Error("Can't retrieve instance of ${T::class}", CommonError.ThrowableError(it)))
            }
        }

    val persistenceRoot: Root get() = storageManager.root() as Root

    override val coroutineContext: CoroutineContext = Dispatchers.IO
}

