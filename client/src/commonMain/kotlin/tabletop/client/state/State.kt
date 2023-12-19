package tabletop.client.state

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.Serializable
import tabletop.client.asset.Assets
import tabletop.client.di.ConnectionDependencies
import tabletop.client.ui.TokenizableDragging
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token
import tabletop.common.user.User

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class State(
    val connectionDependencies: MutableStateFlow<ConnectionDependencies?> = MutableStateFlow(null),
    val connectionJob: MutableStateFlow<Job?> = MutableStateFlow(null),
) {
    val selectedToken: MutableStateFlow<Token<*>?> = MutableStateFlow(null)
    val sceneForeGroundImageScale: MutableStateFlow<Float> = MutableStateFlow(1f)
    val sceneForegroundImagePositionInWindow: MutableStateFlow<Offset?> = MutableStateFlow(null)
    val errors: MutableStateFlow<List<CommonError>> = MutableStateFlow(listOf())
    val tokenizableDragging: MutableStateFlow<TokenizableDragging?> = MutableStateFlow(null)
    val assetDownloads = MutableStateFlow(mapOf<String, Assets.Download>())

    val maybeUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: Either<Error, User?> get() =  either { ensureNotNull(maybeUser.value) { Error("User not logged in", null) } }
    val games: MutableStateFlow<Set<Game<*>>> = MutableStateFlow(setOf())
    val maybeGame: MutableStateFlow<Game<*>?> = MutableStateFlow(null)
    val game get() = either { ensureNotNull(maybeGame.value) { Error("Game not loaded", null) } }
    val currentScene: MutableStateFlow<Scene?> = MutableStateFlow(null)

    @Serializable
    open class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    fun <T : Any> MutableStateFlow<T?>.ensureNotNull() = either { ensureNotNull(value) { Error("Value is null", null) } }
}

