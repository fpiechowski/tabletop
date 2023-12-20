package tabletop.shared.auth

import arrow.core.Either
import kotlinx.serialization.Serializable
import tabletop.shared.error.CommonError
import tabletop.shared.user.User


abstract class Authentication {
    abstract suspend fun authenticate(principal: String, secret: String): Either<Error, User>

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) :
        CommonError()
}