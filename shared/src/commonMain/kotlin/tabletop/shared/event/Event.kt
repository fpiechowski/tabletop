package tabletop.shared.event

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.shared.auth.Credentials
import tabletop.shared.dnd5e.character.Character
import tabletop.shared.error.CommonError
import tabletop.shared.game.Game
import tabletop.shared.geometry.Point
import tabletop.shared.scene.token.Token
import tabletop.shared.user.User

interface Event {

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object
}

@Serializable
sealed interface RequestEvent : Event

@Serializable
sealed interface ResultEvent : Event {
    val shared: Boolean
}

@Serializable
data class AuthenticationRequested(
    val credentialsData: Credentials.UsernamePassword.Data
) : RequestEvent

@Serializable
data class UserAuthenticated(val user: User) : ResultEvent {
    override val shared: Boolean = false
}

@Serializable
data class GameListingRequested(val userId: UUID) : RequestEvent

@Serializable
data class GamesLoaded(val games: Set<Game<*>>) : ResultEvent {
    override val shared: Boolean = false
}

@Serializable
data class GameLoadingRequested(val gameId: UUID) : RequestEvent

@Serializable
data class GameLoaded(val game: Game<*>) : ResultEvent {
    override val shared: Boolean = false
}

@Serializable
data class SceneOpeningRequested(val sceneId: UUID) : RequestEvent

@Serializable
data class SceneOpened(val sceneId: UUID) : ResultEvent {
    override val shared: Boolean = false
}

@Serializable
data class TokenPlacingRequested(
    val gameId: UUID,
    val tokenizableId: UUID,
    val sceneId: UUID,
    val position: Point
) : RequestEvent

@Serializable
data class TokenPlaced(val token: Token<*>) : ResultEvent {
    override val shared: Boolean = false
}

@Serializable
data class CharacterUpdateRequested(val gameId: UUID, val character: Character) : RequestEvent

@Serializable
data class CharacterUpdated(val character: Character) : ResultEvent {
    override val shared: Boolean = true
}
