package tabletop.dnd5e

import kotlinx.uuid.UUID
import tabletop.System


object DnD5e : System("Dungeons & Dragons - 5th Edition") {
    private const val idUUIDValue = "70452e48-ae88-43e3-b3f2-ea17d20b5bc3"
    override val id: UUID = UUID(idUUIDValue)
}
