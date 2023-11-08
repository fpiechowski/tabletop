package tabletop.common.game

import tabletop.common.NamedEntity
import tabletop.common.game.player.Player
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Tokenizable
import tabletop.common.system.System
import tabletop.common.user.GameMaster
import java.io.Serializable
import java.util.*


abstract class Game<T : System> : NamedEntity(), Serializable {

    abstract override val id: UUID
    abstract val system: T
    abstract val gameMaster: GameMaster
    abstract val players: Set<Player>
    abstract val scenes: MutableMap<UUID, Scene>
    abstract val tokenizables: Map<UUID, Tokenizable>
    abstract val chat: Chat

    companion object {
        private const val serialVersionUID = 1L
    }

    data class Listing(
        val games: List<Item>
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }


        data class Item(
            val id: UUID,
            val name: String,
            val systemName: String
        ) : Serializable {
            constructor(game: Game<*>) : this(game.id, game.name, game.system.name)

            companion object {
                private const val serialVersionUID = 1L
            }

        }

    }

    class Chat {
        interface Speaker

        fun <T> send(speaker: Speaker, message: T): Unit = TODO()
    }
}
