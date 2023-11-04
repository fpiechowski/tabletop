package tabletop.common.rpg.character

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.NamedEntity
import tabletop.common.game.Game
import tabletop.common.geometry.Point
import tabletop.common.scene.Scene
import tabletop.common.scene.Token
import tabletop.common.scene.Tokenizable
import tabletop.common.user.Player

@Serializable
abstract class Character : NamedEntity(),
    Tokenizable,
    Game.Chat.Speaker {

    override fun tokenize(scene: Scene, position: Point): Token<Character> =
        Token(
            name,
            position,
            scene,
            this,
            tokenImageFilePath
        )
}

@Serializable
class PlayerCharacter(
    override val name: String,
    override val tokenImageFilePath: String,
    val player: Player,
    override val id: UUID = UUID.generateUUID()
) : Character()


@Serializable
class NonPlayerCharacter(
    override val name: String,
    override val tokenImageFilePath: String,
    override val id: UUID = UUID.generateUUID()
) : Character()
