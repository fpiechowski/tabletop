package tabletop.shared.connection.session

import tabletop.shared.connection.Connection
import tabletop.shared.game.Game
import tabletop.shared.game.player.Player

data class PlayerGameSession(val player: Player?, val game: Game<*>, val connection: Connection)