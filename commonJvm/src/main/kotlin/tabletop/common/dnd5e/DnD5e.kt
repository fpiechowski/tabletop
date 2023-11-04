package tabletop.common.dnd5e

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.game.Game
import tabletop.common.rpg.character.Character
import tabletop.common.scene.Scene
import tabletop.common.system.System
import tabletop.common.user.GameMaster
import tabletop.common.user.Player


object DnD5e : System("Dungeons & Dragons - 5th Edition") {
    private const val idUUIDValue = "70452e48-ae88-43e3-b3f2-ea17d20b5bc3"
    override val id: UUID = UUID(idUUIDValue)

}

class DnD5eGame(
    override val name: String,
    override val system: DnD5e,
    override val gameMaster: GameMaster,
    val characters: MutableSet<Character> = mutableSetOf(),
    override val players: Set<Player> = setOf(),
    override val scenes: MutableMap<UUID, Scene> = mutableMapOf(),
    override val chat: Chat = Chat(),
    override val id: UUID = UUID.generateUUID()
) : Game<DnD5e>()