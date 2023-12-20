package tabletop.shared

import tabletop.shared.game.Game

interface Usable<USER : Usable.User, TARGET : Usable.Target> {
    fun use(game: Game<*>, user: USER, targets: Set<TARGET>)

    interface User
    interface Target
}
