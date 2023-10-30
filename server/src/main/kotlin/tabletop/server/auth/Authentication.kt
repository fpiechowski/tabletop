package tabletop.server.auth

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import arrow.fx.stm.atomically
import tabletop.common.auth.Authentication
import tabletop.common.user.User
import tabletop.server.di.DependenciesAdapter

class AuthenticationAdapter(
    private val dependencies: DependenciesAdapter.ConnectionScope
) : Authentication() {

    override suspend fun authenticate(principal: String, secret: String): Either<Error, User> =
        with(dependencies) {
            either {
                persistence.persistenceRoot.credentials
                    .filterValues { it.principal == principal && it.secret == secret }
                    .keys
                    .firstOrNull()
                    ?.also { atomically { connection.authenticatedUser.put(it) } }
                    .let { ensureNotNull(it) { Error("Invalid credentials", null) } }
            }
        }
}