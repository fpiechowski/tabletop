package tabletop.client.state

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import tabletop.client.asset.Assets
import tabletop.client.di.ConnectionDependencies
import tabletop.client.ui.TokenizableDragging
import tabletop.shared.error.CommonError
import tabletop.shared.game.Game
import tabletop.shared.user.User

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class State(
    val connectionDependencies: MutableStateFlow<ConnectionDependencies?> = MutableStateFlow(null),
    val connectionJob: MutableStateFlow<Job?> = MutableStateFlow(null),
) {
    val scene: SceneState = SceneState()

    val tokenizableDragging: MutableStateFlow<TokenizableDragging?> = MutableStateFlow(null)
    val assetDownloads = MutableStateFlow(mapOf<String, Assets.Download>())

    val maybeUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: Either<Error, User?>
        get() = either {
            ensureNotNull(maybeUser.value) {
                Error(
                    "User not logged in",
                    null
                )
            }
        }
    val games: MutableStateFlow<Set<Game<*>>> = MutableStateFlow(setOf())
    val maybeGame: MutableStateFlow<Game<*>?> = MutableStateFlow(null)
    val game get() = either { ensureNotNull(maybeGame.value) { Error("Game not loaded", null) } }

    fun <T : Any> MutableStateFlow<T?>.ensureNotNull() =
        either { ensureNotNull(value) { Error("Value is null", null) } }

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

fun MutableStateFlow<List<CommonError>>.add(error: CommonError) {
    value += error
}

fun MutableStateFlow<List<CommonError>>.add(throwable: Throwable) {
    value += CommonError.ThrowableError(throwable)
}
