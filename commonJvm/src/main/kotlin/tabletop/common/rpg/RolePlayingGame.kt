package tabletop.common.rpg

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.game.Game
import tabletop.common.rpg.character.Character
import tabletop.common.rpg.character.NonPlayerCharacter
import tabletop.common.rpg.character.PlayerCharacter
import tabletop.common.scene.Scene
import tabletop.common.system.System
import tabletop.common.user.GameMaster
import tabletop.common.user.Player

object RolePlayingSystem : System(
    name = "Generic Role Playing System"
)

class RolePlayingGame(
    override val name: String,
    override val system: RolePlayingSystem,
    override val gameMaster: GameMaster,
    val playerCharacters: MutableSet<PlayerCharacter> = mutableSetOf(),
    val nonPlayerCharacters: MutableSet<NonPlayerCharacter> = mutableSetOf(),
    override val players: Set<Player> = mutableSetOf(),
    override val scenes: MutableMap<UUID, Scene> = mutableMapOf(),
    override val chat: Chat = Chat(),
    override val id: UUID = UUID.generateUUID()
) : Game<RolePlayingSystem>() {
    val characters: Set<Character> get() = playerCharacters + nonPlayerCharacters
}