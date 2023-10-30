package tabletop.client.persistence

import one.microstream.storage.embedded.types.EmbeddedStorage
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.client.settings.Settings
import tabletop.common.persistence.Persistence as CommonPersistence

class Persistence(
    storageManager: EmbeddedStorageManager = EmbeddedStorage.start(Root())
) : CommonPersistence<Persistence.Root>(storageManager) {
    class Root(
        val settings: Settings = Settings()
    )
}