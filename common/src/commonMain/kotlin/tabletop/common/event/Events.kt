package tabletop.common.event

import kotlinx.uuid.UUID
import tabletop.common.auth.Credentials
import tabletop.common.game.Game
import tabletop.common.geometry.Point
import tabletop.common.scene.token.Token
import tabletop.common.user.User


data class AuthenticationRequested(
    val credentialsData: Credentials.UsernamePassword.Data
) :
    RequestEvent

data class UserAuthenticated(val user: User) : ResultEvent {
    override val shared: Boolean = false


}

data class GameListingRequested(val userId: UUID) : RequestEvent

data class GameListingLoaded(val listing: Game.Listing) : ResultEvent {
    override val shared: Boolean = false


}

data class GameLoadingRequested(val gameListingItem: Game.Listing.Item) : RequestEvent

data class GameLoaded(val game: Game<*>) : ResultEvent {
    override val shared: Boolean = false


}

data class SceneOpeningRequested(val sceneId: UUID) : RequestEvent

data class SceneOpened(val sceneId: UUID) : ResultEvent {
    override val shared: Boolean = false


}

data class TokenPlacingRequested(
    val gameId: UUID,
    val tokenizableId: UUID,
    val sceneId: UUID,
    val position: Point
) : RequestEvent

data class TokenPlaced(val token: Token<*>) : ResultEvent {
    override val shared: Boolean = false


}

