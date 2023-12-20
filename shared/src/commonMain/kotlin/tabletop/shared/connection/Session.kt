package tabletop.shared.connection

import tabletop.shared.game.Game

class Session<T : Game<*>>(
    val game: T,
    val connections: MutableSet<Connection>
)