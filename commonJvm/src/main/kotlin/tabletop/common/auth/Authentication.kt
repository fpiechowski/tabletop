package tabletop.common.auth

import arrow.core.Either
import kotlinx.serialization.Serializable
import tabletop.common.error.CommonError
import tabletop.common.user.User


abstract class Authentication {
    abstract suspend fun authenticate(principal: String, secret: String): Either<Error, User>

    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) :
        CommonError()
}