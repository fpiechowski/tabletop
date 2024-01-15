package tabletop.server.demo

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import arrow.optics.copy
import tabletop.shared.demo.*
import tabletop.shared.error.CommonError
import tabletop.server.persistence.Persistence
import tabletop.shared.dnd5e.DnD5e
import tabletop.shared.game.Game
import tabletop.shared.plus
import tabletop.shared.persistence.Persistence as SharedPersistence

@Suppress("UNCHECKED_CAST")
class Demo(private val persistence: Persistence) {

    init {
        storeEntities()
    }


    private fun storeEntities(): Either<SharedPersistence.Error, Unit> = either {
        catch({
            with(persistence) {
                val users = listOf(demoGmUser, demoPlayerUser).onEach { user ->
                    Persistence.Root.users.modify(persistenceRoot) { it + (user.id to user) }
                        .persist()
                }

                val credentials = listOf(
                    demoGmUser to demoGmUserCredentials,
                    demoPlayerUser to demoPlayerUserCredentials
                ).onEach { credentialsUserPair ->
                    persistenceRoot.copy(credentials = persistenceRoot.credentials.plus(credentialsUserPair))
                        .persist()
                }.map { it.second }

                val games = listOf(demoGame, demoGame2)
                    .map {
                        Game.scenes.modify(it) {  it + (demoScene.id to demoScene)}
                            .let {
                                Game.system<DnD5e>().compose(DnD5e.characters).modify(it as Game<DnD5e>) {
                                    it + demoCharacter + demoNonPlayerCharacter
                                }
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
        })
        {
            raise(SharedPersistence.Error("Error on storing demo entities", CommonError.ThrowableError(it)))
        }
    }
}