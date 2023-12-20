package tabletop.server.state

import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.shared.connection.Connection
import tabletop.shared.game.Game

data class State(
    val connections: MutableStateFlow<Set<Connection>> = MutableStateFlow(setOf()),
    val connectionToGame: MutableStateFlow<Map<Connection, Game<*>>> = MutableStateFlow(mapOf())
) : tabletop.shared.state.State