package tabletop.client.state

import arrow.fx.stm.TMVar
import kotlinx.coroutines.runBlocking
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.user.User

class State(
    val gameListing: TMVar<Game.Listing>,
    val game: TMVar<Game<*>>,
    val user: TMVar<User>,
    val currentScene: TMVar<Scene>
) {


    companion object {

        fun empty() = runBlocking { State(TMVar.empty(), TMVar.empty(), TMVar.empty(), TMVar.empty()) }

        class Error(override val message: String?, override val cause: CommonError?) : CommonError()
    }
}