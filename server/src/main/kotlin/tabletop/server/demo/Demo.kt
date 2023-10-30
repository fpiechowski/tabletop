package tabletop.server.demo

import arrow.core.Either
import arrow.core.raise.either
import tabletop.common.demo.*
import tabletop.server.persistence.Persistence
import tabletop.common.persistence.Persistence as CommonPersistence

class Demo(private val persistence: Persistence) {

    init {
        storeEntities()
    }

    private fun storeEntities(): Either<CommonPersistence.Error, Unit> = either {
        with(persistence) {
            val users = listOf(demoGmUser, demoPlayerUser)
                .onEach { user ->
                    persistenceRoot.users[user.id] = user
                }

            val credentials = listOf(demoGmUserCredentials to demoGmUser, demoPlayerUserCredentials to demoPlayerUser)
                .onEach { credentialsUserPair ->
                    persistenceRoot.credentials[credentialsUserPair.second] = credentialsUserPair.first
                }.map { it.first }

            val games = listOf(demoGame, demoGame2)
                .onEach { game ->
                    persistenceRoot.games[game.id] = game
                }

            (users + credentials + games).forEach {
                it.persist()
            }
        }
    }
}