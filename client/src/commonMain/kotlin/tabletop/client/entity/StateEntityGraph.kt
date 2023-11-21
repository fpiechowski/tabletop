package tabletop.client.entity

import arrow.core.Either
import arrow.core.raise.either
import tabletop.client.state.State
import tabletop.common.entity.EntityGraph
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token
import tabletop.common.scene.token.Tokenizable

class StateEntityGraph(private val state: State) : EntityGraph {

    class Error(override val message: String?, override val cause: CommonError?) : CommonError() {
        companion object {
            fun cantRetrieve(cause: CommonError?): Error = Error("Can't retrieve Token's Scene", cause)
        }
    }

    override val Token<*>.scene: Either<Error, Scene>
        get() =
            either {
                state.run {
                    game.bind()
                        .scenes.bind()
                        .current
                        .get(sceneId).bind()
                }
            }.mapLeft {
                Error.cantRetrieve(it)
            }

    override fun <T : Tokenizable> Token<T>.tokenizable(): Either<State.Error, Tokenizable> =
        either {
            state.run {
                TODO()
            }
        }

    override val Game<*>.tokenizables: Set<Tokenizable>
        get() = TODO()
    override val Scene.game: Either<CommonError, Game<*>>
        get() = TODO("Not yet implemented")

}

