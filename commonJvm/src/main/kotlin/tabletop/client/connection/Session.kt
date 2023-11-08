package tabletop.client.connection

import tabletop.common.connection.Connection
import tabletop.common.game.Game

class Session<T : Game<*>>(
    val game: T,
    val connections: MutableSet<Connection>
)