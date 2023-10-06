package tabletop

import kotlinx.uuid.UUID
import tabletop.character.Character
import tabletop.scene.Scene
import tabletop.user.GameMaster
import tabletop.user.Player


class Game<T : System>(
    override val name: String,
    val system: T,
    val gameMaster: GameMaster,
    val players: Set<Player> = setOf(),
    val scenes: MutableMap<UUID, Scene> = mutableMapOf(),
    val characters: MutableSet<Character> = mutableSetOf(),
    val items: Set<Item<*, *>> = setOf(),
    val chat: Chat = Chat()
) : Entity() {
    class Chat {
        interface Speaker

        fun <T> send(speaker: Speaker, message: T): Unit = TODO()
    }

    interface Context<T : System> {
        val game: Game<T>
    }
}
