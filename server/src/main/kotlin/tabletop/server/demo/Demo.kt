package tabletop.server.demo

import arrow.core.raise.Raise
import tabletop.common.demo.*
import tabletop.server.persistence.Persistence
import tabletop.server.persistence.persist


context (Raise<Persistence.Error>, Persistence)
fun storeDemoEntities() {
    val users = listOf(demoGmUser, demoPlayerUser)
        .onEach { user ->
            persistenceRoot.users[user.id] = user
        }

    val credentials = listOf(demoGmUserCredentials to demoGmUser, demoPlayerUserCredentials to demoPlayerUser)
        .onEach { credentialsUserPair ->
            persistenceRoot.credentials[credentialsUserPair.second] = credentialsUserPair.first
        }.map { it.first }

    val games = listOf(demoGame)
        .onEach { game ->
            persistenceRoot.games[game.id] = game
        }

    (users + credentials + games).forEach {
        it.persist()
    }
}