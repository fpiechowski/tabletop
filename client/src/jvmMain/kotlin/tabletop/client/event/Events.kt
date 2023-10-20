package tabletop.client.event

import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.user.User

object EscapeKeyDown : Event()
class LoadingGameAttempted(val gameListingItem: Game.Listing.Item) : Event()

class UserAuthenticated(val user: User) : Event()

class ConnectionAttempted(val host: String, val port: Int, val credentialsData: Credentials.UsernamePassword.Data) :
    Event()