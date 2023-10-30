package tabletop.server.persistence

import kotlinx.uuid.UUID
import one.microstream.storage.embedded.types.EmbeddedStorage
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.user.User
import tabletop.common.persistence.Persistence as CommonPersistence

class Persistence(
    storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root())
) : CommonPersistence<Persistence.Root>(storageManager) {
    class Root(
        val games: MutableMap<UUID, Game> = mutableMapOf(),
        val users: MutableMap<UUID, User> = mutableMapOf(),
        val credentials: MutableMap<User, Credentials.UsernamePassword> = mutableMapOf()
    )
}