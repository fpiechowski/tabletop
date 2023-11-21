package tabletop.common.scene

import arrow.core.Either
import arrow.core.raise.either
import arrow.optics.Lens
import arrow.optics.PLens
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.NamedEntity
import tabletop.common.error.CommonError
import tabletop.common.scene.token.Token

@Serializable
data class Scene(
    override val name: String,
    val gameId: UUID,
    val foregroundImagePath: String?,
    val tokens: Set<Token<*>> = setOf(),
    val grid: Grid? = null,
    override val id: UUID = UUID.generateUUID()
) : NamedEntity() {

    companion object {
        val tokens: Either<CommonError, Lens<Scene, Set<Token<*>>>> = either {
            PLens(
                get = { scene -> scene.tokens },
                set = { scene, tokens ->
                    scene.copy(tokens = tokens)
                }
            )
        }
    }

    @Serializable
    class Grid(
        val size: Int
    )
}
