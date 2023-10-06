package tabletop

import tabletop.scene.Tokenizable

abstract class Item<USER : Usable.User, TARGET : Usable.Targetable>
    : Entity(), Usable<USER, TARGET>, Usable.Targetable, Tokenizable {
    override fun use(game: Game<*>, user: USER, targets: Set<TARGET>) {
        game.chat.send(TODO(), this)
    }
}
