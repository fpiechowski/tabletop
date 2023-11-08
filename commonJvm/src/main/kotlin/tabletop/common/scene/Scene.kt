package tabletop.common.scene

import tabletop.common.NamedEntity
import tabletop.common.scene.token.Token
import java.io.Serializable
import java.util.*

class Scene(
    override val name: String,
    val foregroundImagePath: String?,
    val tokens: MutableMap<UUID, Token<*>> = mutableMapOf(),
    val grid: Grid? = null,
    override val id: UUID = UUID.randomUUID()
) : NamedEntity(), Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }


    class Grid(
        val size: Int
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}
