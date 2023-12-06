package tabletop.client.state

import androidx.compose.ui.ExperimentalComposeUiApi
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import tabletop.client.di.ConnectionDependencies
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.user.User

@ExperimentalComposeUiApi
class State(
    val connectionDependencies: MutableStateFlow<ConnectionDependencies?> = MutableStateFlow(null),
    val connectionJob: MutableStateFlow<Job?> = MutableStateFlow(null),
) {
    val maybeUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: Either<Error, User?> get() =  either { ensureNotNull(maybeUser.value) { Error("User not logged in", null) } }
    val games: MutableStateFlow<Set<Game<*>>> = MutableStateFlow(setOf())
    val maybeGame: MutableStateFlow<Game<*>?> = MutableStateFlow(null)
    val game get() = either { ensureNotNull(maybeGame.value) { Error("Game not loaded", null) } }
    val currentScene: MutableStateFlow<Scene?> = MutableStateFlow(null)

    @Serializable
    open class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

