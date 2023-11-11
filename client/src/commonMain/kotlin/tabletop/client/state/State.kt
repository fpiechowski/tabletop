package tabletop.client.state

import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import dev.fritz2.validation.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.di.Dependencies
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.user.User

class State(
    val gameListing: Store<Game.Listing?> = storeOf(null, Job()),
    val game: MutableStateFlow<Game<*>?> = MutableStateFlow(null),
    val user: MutableStateFlow<User?> = MutableStateFlow(null),
    val currentScene: MutableStateFlow<Scene?> = MutableStateFlow(null),
    val connectionScope: MutableStateFlow<Dependencies.ConnectionScope?> = MutableStateFlow(null),
    val connectionJob: MutableStateFlow<Job?> = MutableStateFlow(null)
) {

    val authenticatedUser = storeOf(null, Job())

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}