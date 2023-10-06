package tabletop.persistence

import kotlinx.uuid.UUID
import one.microstream.storage.embedded.types.EmbeddedStorage
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.Entity
import tabletop.Game
import tabletop.user.User


object Persistence {
    class Root {
        val games: MutableMap<UUID, Game<*>> = mutableMapOf()
        val users: MutableMap<UUID, User> = mutableMapOf()
    }

    val storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root())
    val persistenceRoot get() = storageManager.root() as Root

    init {
        storageManager.storeRoot()
    }

    class Exception(override val message: String?, override val cause: Throwable? = null) : Throwable(message, cause)
}

context (Persistence)
fun Entity.persist() = storageManager.store(this)
