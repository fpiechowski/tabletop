package tabletop.common.dnd5e

import arrow.optics.Lens
import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
import tabletop.common.user.User

@Serializable
data class DnD5e(
    override val id: UUID = UUID(defaultIdValue),
    override val name: String = "Dungeons & Dragons - 5th Edition"
) : System() {

    companion object {
        private const val defaultIdValue = "70452e48-ae88-43e3-b3f2-ea17d20b5bc3"
    }
}

@Serializable
@optics
data class DnD5eGame(
    override val name: String,
    val playerCharacters: Set<PlayerCharacter> = setOf(),
    val nonPlayerCharacters: Set<NonPlayerCharacter> = setOf(),
    override val system: DnD5e,
    override val players: Set<Player> = setOf(),
    override val scenes: Set<Scene> = setOf(),
    override val tokenizables: Set<Tokenizable> = setOf(),
    override val gameMaster: GameMaster,
    override val id: UUID = UUID.generateUUID(),
) : Game<DnD5e>() {

    companion object {
        val nonPlayerCharactersLens: Lens<DnD5eGame, Set<NonPlayerCharacter>> = Lens(
            { it.nonPlayerCharacters },
            { game, nonPlayerCharacters -> game.copy(nonPlayerCharacters = nonPlayerCharacters) }
        )
        val playerCharactersLens: Lens<DnD5eGame, Set<PlayerCharacter>> = Lens(
            { it.playerCharacters },
            { game, playerCharacters -> game.copy(playerCharacters = playerCharacters) }
        )
    }

    constructor(
        name: String,
        playerCharacters: Set<PlayerCharacter> = setOf(),
        nonPlayerCharacters: Set<NonPlayerCharacter> = setOf(),
        system: DnD5e,
        players: Set<Player> = setOf(),
        scenes: Set<Scene> = setOf(),
        tokenizables: Set<Tokenizable> = setOf(),
        initialGameMasterUser: User,
        id: UUID = UUID.generateUUID()
    ) : this(
        id = id,
        name = name,
        gameMaster = GameMaster("Game Master", id, initialGameMasterUser),
        playerCharacters = playerCharacters,
        nonPlayerCharacters = nonPlayerCharacters,
        system = system,
        players = players,
        scenes = scenes,
        tokenizables = tokenizables
    )


    @Transient
    override val chat: Chat = Chat()
}