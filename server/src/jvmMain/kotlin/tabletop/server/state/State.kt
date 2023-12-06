package tabletop.server.state

import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.common.connection.Connection
import tabletop.common.game.Game

data class State(
    val connections: MutableStateFlow<Set<Connection>> = MutableStateFlow(setOf()),
    val connectionToGame: MutableStateFlow<Map<Connection, Game<*>>> = MutableStateFlow(mapOf())
) : tabletop.common.state.State