package tabletop.client.game

import arrow.fx.stm.atomically
import tabletop.client.di.Dependencies

suspend fun game() = Dependencies.await().state.game.let { atomically { it.read() } }
