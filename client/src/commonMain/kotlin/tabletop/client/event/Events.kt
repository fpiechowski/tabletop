package tabletop.client.event

import korlibs.event.EventType
import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.scene.Scene
import tabletop.common.user.User

class LoadingGameAttempted(val gameListingItem: Game.Listing.Item) : Event()

class UserAuthenticated(val user: User) : UIEvent<UserAuthenticated>(UserAuthenticated) {
    companion object : EventType<UserAuthenticated>
}

class ConnectionAttempted(val host: String, val port: Int, val credentialsData: Credentials.UsernamePassword.Data) :
    Event()

class GameLoaded(val game: Game) : UIEvent<GameLoaded>(GameLoaded) {
    companion object : EventType<GameLoaded>
}

class SceneOpened(val scene: Scene) : UIEvent<SceneOpened>(SceneOpened) {
    companion object : EventType<SceneOpened>
}