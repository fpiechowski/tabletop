package tabletop.common.entity

import arrow.core.Either
import kotlinx.uuid.UUID
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.game.player.Player
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token
import tabletop.common.scene.token.Tokenizable
import tabletop.common.system.System
import tabletop.common.user.GameMaster
import tabletop.common.user.User
import kotlin.reflect.KClass


abstract class Entity : Identifiable<UUID>


abstract class NamedEntity : Entity(), Named

interface Named {
    val name: String
}

interface Identifiable<T> {
    val id: T
}

interface EntityGraph {
    fun <T : Tokenizable> Token<T>.tokenizable(): Either<CommonError, Tokenizable>
    val Game<*>.tokenizables: Set<Tokenizable>
    val Scene.game: Either<CommonError, Game<*>>
    val Token<*>.scene: Either<CommonError, Scene>
}