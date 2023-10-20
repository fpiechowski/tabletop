package tabletop.common.rpg.item

import kotlinx.serialization.Serializable
import tabletop.common.Game
import tabletop.common.NamedEntity
import tabletop.common.Usable
import tabletop.common.scene.Tokenizable

@Serializable
abstract class Item<USER : Usable.User, TARGET : Usable.Targetable>
    : NamedEntity(), Usable<USER, TARGET>, Usable.Targetable, Tokenizable {
    override fun use(game: Game, user: USER, targets: Set<TARGET>) {
        // game.chat.send(thus, this)
    }
}