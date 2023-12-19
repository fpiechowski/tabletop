package tabletop.common.dnd5e.magic

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.Entity

@Serializable
class Spell(override val name: String,
            override val image: String? = null,
            override val id: UUID = UUID.generateUUID()) : Entity()
