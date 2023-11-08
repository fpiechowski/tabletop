package tabletop.common.connection.session

import tabletop.common.connection.Connection
import tabletop.common.game.Game
import tabletop.common.game.player.Player

data class PlayerGameSession(val player: Player?, val game: Game<*>, val connection: Connection)