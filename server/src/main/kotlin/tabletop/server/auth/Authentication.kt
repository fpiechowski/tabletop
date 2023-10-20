package tabletop.server.auth

import arrow.core.raise.Raise
import arrow.core.raise.ensureNotNull
import tabletop.common.auth.Authentication
import tabletop.common.user.User
import tabletop.server.persistence.Persistence


context(Raise<Authentication.Error>, Authentication, Persistence)
fun authenticate(principal: String, secret: String): User = persistenceRoot.credentials
    .filterValues { it.principal == principal && it.secret == secret }
    .keys
    .firstOrNull()
    .let { ensureNotNull(it) { Authentication.Error("Invalid credentials") } }
