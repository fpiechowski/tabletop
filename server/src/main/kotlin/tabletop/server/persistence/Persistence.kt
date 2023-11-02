package tabletop.server.persistence

import kotlinx.uuid.UUID
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.user.User
import tabletop.common.persistence.Persistence as CommonPersistence

class Persistence(
    override val storageManager: EmbeddedStorageManager,
) : CommonPersistence<Persistence.Root>() {
    class Root(
        val games: MutableMap<UUID, Game> = mutableMapOf(),
        val users: MutableMap<UUID, User> = mutableMapOf(),
        val credentials: MutableMap<User, Credentials.UsernamePassword> = mutableMapOf()
    )

    override val persistenceRoot: Root get() = storageManager.root() as Root
}