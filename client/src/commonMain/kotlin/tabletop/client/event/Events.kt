package tabletop.client.event

import tabletop.shared.auth.Credentials
import tabletop.shared.error.CommonError
import tabletop.shared.event.Event

class ConnectionAttempted(val host: String, val port: Int, val credentialsData: Credentials.UsernamePassword.Data) :
    Event

class ConnectionEnded(val cause: CommonError) : Event