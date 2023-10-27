package tabletop.client.state

import arrow.fx.stm.TMVar
import tabletop.common.Game
import tabletop.common.error.CommonError
import tabletop.common.user.User

class State(
    val gameListing: TMVar<Game.Listing>,
    val game: TMVar<Game>,
    val user: TMVar<User>
) {


    companion object {
        class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
    }
}