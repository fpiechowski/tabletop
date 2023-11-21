package tabletop.client.state

import arrow.core.Either
import arrow.core.raise.either
import dev.fritz2.core.Lens
import dev.fritz2.core.Store
import dev.fritz2.core.lensOf
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.client.di.Dependencies
import tabletop.client.ensureNotNull
import tabletop.client.idLensOf
import tabletop.client.toFritz2
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.dnd5e.character.NonPlayerCharacter
import tabletop.common.dnd5e.character.PlayerCharacter
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token
import tabletop.common.system.System
import tabletop.common.user.User

class State(
    val connectionScope: MutableStateFlow<Dependencies.ConnectionScope?> = MutableStateFlow(null),
    val connectionJob: MutableStateFlow<Job?> = MutableStateFlow(null),
) {
    val user: Store<User?> = storeOf(null, Job())
    val games: Store<Set<Game<*>>> = storeOf(setOf(), Job())
    val maybeGame: Store<Game<*>?> = storeOf(null, Job())
    val game: Either<Error, Store<Game<*>>> = maybeGame.ensureNotNull()
    val currentScene: Store<Scene?> = storeOf(null, Job())

    inline fun <reified T : System, G : Game<T>> game(): Either<Error, Store<G>> = either {
        game.bind().map(gameWithSystemLens<T, G>().bind())
    }

    val Store<Game<*>>.scenes: Either<Error, Store<Set<Scene>>>
        get() = either {
            map(gameScenesLens.bind())
        }

    fun Store<Game<*>>.scene(id: UUID): Either<Error, Store<Scene>> = either {
        scenes.bind()
            .map(idLensOf(id)).ensureNotNull().bind()
    }

    val Store<DnD5eGame>.playerCharacters: Store<Set<PlayerCharacter>>
        get() = map(gamePlayerCharactersLens)


    val Store<DnD5eGame>.nonPlayerCharacters: Store<Set<NonPlayerCharacter>>
        get() = map(gameNonPlayerCharactersLens)

    val Store<Scene>.tokens: Either<Error, Store<Set<Token<*>>>>
        get() = either {
            map(Scene.tokens.mapLeft { Error("Error on obtaining scene tokens lens", it) }.bind().toFritz2("sceneTokens"))
        }

    val <T : System> Store<Game<T>>.system: Either<Error, Store<T>>
        get() = either {
            map(gameSystemLens<T>().bind())
        }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : System, G : Game<T>> gameWithSystemLens(): Either<Error, Lens<Game<*>, G>> =
        either {
            Game.withSystem<T, G>().mapLeft { Error("Error on obraining game system lens", it) }.bind().toFritz2("gameSystem")
        }

    private val sceneTokensLens: Either<Error, Lens<Scene, Set<Token<*>>>> =
        either {
            Scene.tokens.mapLeft { Error("Error on obtaining scene tokens lens", it) }.bind().toFritz2("sceneTokens")
        }

    private val gameScenesLens: Either<Error, Lens<Game<*>, Set<Scene>>> =
        either {
            Game.scenes.mapLeft { Error("Error on obtaining game scenes lens", it) }.bind().toFritz2("gameScenes")
        }


    @Suppress("UNCHECKED_CAST")
    private fun <T : System> gameSystemLens(): Either<Error, Lens<Game<T>, T>> =
        either {
            Game.system<T>().mapLeft { Error("Can't obtain game system lens", it) }.bind().toFritz2("gameSystem")
        }

    private fun gameSceneLens(sceneId: UUID): Either<Error, Lens<Game<*>, Scene?>> = either {
        gameScenesLens.bind() + idLensOf(sceneId)
    }

    private val gamePlayerCharactersLens =
        lensOf<DnD5eGame, Set<PlayerCharacter>>(
            "gamePlayerCharacters",
            {
                it.playerCharacters
            },
            { game, playerCharacters ->
                game.copy(playerCharacters = playerCharacters)
            })


    private val gameNonPlayerCharactersLens =
        lensOf<DnD5eGame, Set<NonPlayerCharacter>>(
            "gameNonPlayerCharacters",
            {
                it.nonPlayerCharacters
            },
            { game, npcs ->
                game.copy(nonPlayerCharacters = npcs)
            })

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified D : Any> Store<D?>.ensureNotNull(): Either<Error, Store<D>> =
        ensureNotNull { Error("${D::class} not loaded", null) } as Either<Error, Store<D>>


    @Serializable
    open class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

