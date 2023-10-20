package tabletop.common.rpg.character

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Game
import tabletop.common.NamedEntity
import tabletop.common.scene.Tokenizable

@Serializable
open class Character(override val name: String, override val id: UUID = UUID.generateUUID()) : NamedEntity(),
    Tokenizable,
    Game.Chat.Speaker
