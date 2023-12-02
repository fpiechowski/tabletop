package tabletop.common.dnd5e

import arrow.optics.optics
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.dnd5e.character.NonPlayerCharacter
import tabletop.common.dnd5e.character.PlayerCharacter
import tabletop.common.entity.Entity
import tabletop.common.game.Game
import tabletop.common.game.player.Player
import tabletop.common.scene.Scene
import tabletop.common.scene.token.TokenizableEntity
import tabletop.common.system.System
import tabletop.common.user.GameMaster
import tabletop.common.user.User

@Serializable
data class DnD5e(
    override val id: UUID = UUID(defaultIdValue),
    override val name: String = "Dungeons & Dragons - 5th Edition",
    override val image: String? = null
) : System() {

    companion object {
        private const val defaultIdValue = "70452e48-ae88-43e3-b3f2-ea17d20b5bc3"
    }
}

@Serializable
@optics
data class DnD5eGame(
    override val name: String,
    val playerCharacters: Map<UUID, PlayerCharacter> = mapOf(),
    val nonPlayerCharacters: Map<UUID, NonPlayerCharacter> = mapOf(),
    override val system: DnD5e,
    override val players: Map<UUID, Player> = mapOf(),
    override val scenes: Map<UUID, Scene> = mapOf(),
    override val tokenizables: Map<UUID, TokenizableEntity> = mapOf(),
    override val gameMaster: GameMaster,
    override val image: String? = null,
    override val id: UUID = UUID.generateUUID(),
) : Game<DnD5e>() {

    companion object {}

    constructor(
        name: String,
        playerCharacters: Map<UUID, PlayerCharacter> = mapOf(),
        nonPlayerCharacters: Map<UUID, NonPlayerCharacter> = mapOf(),
        system: DnD5e,
        players: Map<UUID, Player> = mapOf(),
        scenes: Map<UUID, Scene> = mapOf(),
        tokenizables: Map<UUID, TokenizableEntity> = mapOf(),
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
    override val entities: Map<UUID, Entity>
        get() = playerCharacters + nonPlayerCharacters + players + scenes + tokenizables + gameMaster.let { it.id to it }
}