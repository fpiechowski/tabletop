package tabletop.server.demo

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.optics.copy
import tabletop.common.demo.*
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.dnd5e.nonPlayerCharacters
import tabletop.common.dnd5e.scenes
import tabletop.common.error.CommonError
import tabletop.server.persistence.Persistence
import tabletop.server.persistence.credentials
import tabletop.server.persistence.games
import tabletop.server.persistence.users

class Demo(private val persistence: Persistence) {

    init {
        storeEntities()
    }

    private fun storeEntities(): Either<Persistence.Error, Unit> = either {
        catch({
            with(persistence) {
                val users = listOf(demoGmUser, demoPlayerUser).onEach { user ->
                    persistenceRoot.copy {
                        Persistence.Root.users set persistenceRoot.users + (user.id to user)
                    }.persist()
                }

                val credentials = listOf(
                    demoGmUser to demoGmUserCredentials,
                    demoPlayerUser to demoPlayerUserCredentials
                ).onEach { credentialsUserPair ->
                    persistenceRoot.copy {
                        Persistence.Root.credentials set persistenceRoot.credentials.plus(credentialsUserPair)
                    }.persist()
                }.map { it.second }

                val games = listOf(demoGame, demoGame2)
                    .map {
                        it.copy {
                            DnD5eGame.scenes set it.scenes + demoScene
                            DnD5eGame.nonPlayerCharacters set it.nonPlayerCharacters + demoNonPlayerCharacter
                        }
                    }.onEach { game ->
                        persistenceRoot.copy {
                            Persistence.Root.games set persistenceRoot.games + (game.id to game)
                        }.persist()
                    }

                (users + credentials + games).forEach {
                    it.persist()
                }
            }
        }) {
            raise(Persistence.Error("Error on storing demo entities", CommonError.ThrowableError(it)))
        }
    }
}