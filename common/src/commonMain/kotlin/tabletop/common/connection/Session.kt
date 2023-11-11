package tabletop.common.connection

import tabletop.common.game.Game

class Session<T : Game<*>>(
    val game: T,
    val connections: MutableSet<Connection>
)