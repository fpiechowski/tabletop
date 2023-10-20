package tabletop.server.persistence

import arrow.core.raise.Raise
import arrow.core.raise.catch
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import one.microstream.storage.embedded.types.EmbeddedStorage
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.error.CommonError
import tabletop.common.user.User


object Persistence {
    val storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root)
    val persistenceRoot = Root

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


context (Raise<Persistence.Error>, Persistence)
fun <T> T.persist() = catch({
    storageManager.store(this)
}) {
    raise(Persistence.Error("Can't store entity $this", CommonError.ThrowableError(it)))
}

context (Raise<Persistence.Error>, Persistence)
fun <T> retrieve(get: Persistence.Root.() -> T): T = catch({
    persistenceRoot.let(get)
}) {
    raise(Persistence.Error("Can't retrieve entity", CommonError.ThrowableError(it)))
}