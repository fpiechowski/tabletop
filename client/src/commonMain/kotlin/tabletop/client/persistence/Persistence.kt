package tabletop.client.persistence

import one.microstream.storage.embedded.types.EmbeddedStorage
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import tabletop.client.settings.Settings
import tabletop.common.persistence.Persistence as CommonPersistence

class Persistence(
    override val persistenceRoot: Root = Root(),
    override val storageManager: EmbeddedStorageManager = EmbeddedStorage.start(persistenceRoot)
) : CommonPersistence<Persistence.Root>() {


    init {
        storageManager.storeRoot()
    }

    class Root(
        val settings: Settings = Settings()
    )
}

