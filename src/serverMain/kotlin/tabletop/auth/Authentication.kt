package tabletop.auth

import arrow.core.raise.Raise
import tabletop.CommonError
import tabletop.persistence.Persistence


object Authentication {
    class Error(override val message: String?, override val cause: CommonError? = null) :
        CommonError(message, cause)
}

context(Raise<Authentication.Error>, Authentication, Persistence)
fun <P, S> Credentials<P, S>.authenticate(): Unit = persistenceRoot.users
    .values
    .any { it.credentials == this }
    .let {
        if (!it) raise(Authentication.Error("Invalid credentials"))
    }
