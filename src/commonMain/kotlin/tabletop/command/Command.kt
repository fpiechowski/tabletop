package tabletop.command

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.CommonError
import tabletop.Entity
import tabletop.geometry.Point
import kotlin.reflect.KClass

@Serializable
sealed class Command {
    class EntityNotFoundException<T : Entity>(gameId: UUID, klass: KClass<T>) :
        Exception("${klass.simpleName} for ID $gameId not found")

    open class Exception(override val message: String?, override val cause: CommonError? = null) :
        CommonError(message, cause)

    @Serializable
    data class Move<T : Number>(
        val gameId: UUID,
        val sceneId: UUID,
        val tokenId: UUID,
        val destination: Point
    ) : Command()
}

