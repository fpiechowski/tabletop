package tabletop.client.state

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.user.User

class State(
    var gameListing: MutableStateFlow<Game.Listing?> = MutableStateFlow(null),
    var game: MutableStateFlow<Game<*>?> = MutableStateFlow(null),
    var user: MutableStateFlow<User?> = MutableStateFlow(null),
    var currentScene: MutableStateFlow<Scene?> = MutableStateFlow(null),
    var connectionJob: MutableStateFlow<Job?> = MutableStateFlow(null)
) {

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}