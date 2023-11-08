package tabletop.client.event

import korlibs.event.EventType
import tabletop.common.event.GameListingLoaded
import tabletop.common.event.SceneOpened
import tabletop.common.event.TokenPlaced
import tabletop.common.event.UserAuthenticated
import tabletop.common.scene.Scene


class GameListingLoadedUIEvent(
    override val event: GameListingLoaded
) : UIEvent<GameListingLoadedUIEvent, GameListingLoaded>(event) {

    companion object : EventType<GameListingLoadedUIEvent>

    override val type = GameListingLoadedUIEvent
}

class UserAuthenticatedUIEvent(
    override val event: UserAuthenticated
) : UIEvent<UserAuthenticatedUIEvent, UserAuthenticated>(event) {

    companion object : EventType<UserAuthenticatedUIEvent>

    override val type = UserAuthenticatedUIEvent
}

class TokenPlacedUIEvent(
    override val event: TokenPlaced
) : UIEvent<TokenPlacedUIEvent, TokenPlaced>(event) {

    companion object : EventType<TokenPlacedUIEvent>

    override val type = TokenPlacedUIEvent
}

class SceneOpenedUIEvent(
    override val event: SceneOpened,
    val scene: Scene
) : UIEvent<SceneOpenedUIEvent, SceneOpened>(event) {

    companion object : EventType<SceneOpenedUIEvent>

    override val type = SceneOpenedUIEvent
}