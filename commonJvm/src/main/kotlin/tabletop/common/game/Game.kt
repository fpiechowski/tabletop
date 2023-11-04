package tabletop.common.game

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.command.Command
import tabletop.common.scene.Scene
import tabletop.common.system.System
import tabletop.common.user.GameMaster
import tabletop.common.user.Player


@Serializable
abstract class Game<T : System> : NamedEntity(), Command.Result.Data {

    override val id: UUID = UUID.generateUUID()
    abstract val system: T
    abstract val gameMaster: GameMaster
    open val players: Set<Player> = setOf()
    open val scenes: MutableMap<UUID, Scene> = mutableMapOf()
    open val chat: Chat = Chat()

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
            constructor(game: Game<*>) : this(game.id, game.name, game.system.name)
        }

    }

    @Serializable
    class Chat {
        interface Speaker

        fun <T> send(speaker: Speaker, message: T): Unit = TODO()
    }
}
