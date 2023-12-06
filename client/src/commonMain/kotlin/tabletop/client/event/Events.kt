package tabletop.client.event

import tabletop.common.auth.Credentials
import tabletop.common.error.CommonError
import tabletop.common.event.Event

class ConnectionAttempted(val host: String, val port: Int, val credentialsData: Credentials.UsernamePassword.Data) :
    Event

class ConnectionEnded(val cause: CommonError) : Event