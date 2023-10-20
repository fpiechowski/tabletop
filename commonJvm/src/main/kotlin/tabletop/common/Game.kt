package tabletop.common

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.command.Command
import tabletop.common.rpg.character.Character
import tabletop.common.rpg.item.Item
import tabletop.common.scene.Scene
import tabletop.common.user.GameMaster
import tabletop.common.user.Player


@Serializable
class Game(
    override val name: String,
    val system: System,
    val gameMaster: GameMaster,
    val players: Set<Player> = setOf(),
    val scenes: MutableMap<UUID, Scene> = mutableMapOf(),
    val characters: MutableSet<Character> = mutableSetOf(),
    val items: Set<Item<*, *>> = setOf(),
    val chat: Chat = Chat(),
    override val id: UUID = UUID.generateUUID()
) : NamedEntity(), Command.Result.Data {

    companion object

    @Serializable
    data class Listing(
        val games: List<Item>
    ) : Command.Result.Data {
        @Serializable
        data class Item(
            val id: UUID,
            val name: String,
            val systemName: String
        ) {
            constructor(game: Game) : this(game.id, game.name, game.system.name)
        }

    }

    @Serializable
    class Chat {
        interface Speaker

        fun <T> send(speaker: Speaker, message: T): Unit = TODO()
    }
}
