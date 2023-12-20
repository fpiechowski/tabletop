package tabletop.shared.dnd5e.magic

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.shared.entity.Entity

@Serializable
class Spell(override val name: String,
            override val image: String? = null,
            override val id: UUID = UUID.generateUUID()) : Entity()
