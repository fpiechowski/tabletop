package tabletop.client.event

import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.user.User

class LoadingGameAttempted(val gameListingItem: Game.Listing.Item) : ConnectionEvent()

class UserAuthenticated(val user: User) : ConnectionEvent()

class ConnectionAttempted(val host: String, val port: Int, val credentialsData: Credentials.UsernamePassword.Data) :
    Event()