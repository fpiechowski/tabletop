package tabletop.common.dnd5e

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dnd5e.character.NonPlayerCharacter
import tabletop.common.dnd5e.character.PlayerCharacter
import tabletop.common.game.Game
import tabletop.common.game.player.Player
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Tokenizable
import tabletop.common.system.System
import tabletop.common.user.GameMaster

object DnD5e : System("Dungeons & Dragons - 5th Edition") {
    private fun readResolve(): Any = DnD5e

    private const val idUUIDValue = "70452e48-ae88-43e3-b3f2-ea17d20b5bc3"
    override val id: UUID = UUID(idUUIDValue)
}

class DnD5eGame(
    override val name: String,
    override val gameMaster: GameMaster,
    val playerCharacters: MutableSet<PlayerCharacter> = mutableSetOf(),
    val nonPlayerCharacters: MutableSet<NonPlayerCharacter> = mutableSetOf(),
    override val players: Set<Player> = setOf(),
    override val scenes: MutableMap<UUID, Scene> = mutableMapOf(),
    override val id: UUID = UUID.generateUUID()
) : Game<DnD5e>() {
    override val system: DnD5e = DnD5e

    override val chat: Chat = Chat()

    override val tokenizables: Map<UUID, Tokenizable> = (playerCharacters + nonPlayerCharacters).associateBy { it.id }
}