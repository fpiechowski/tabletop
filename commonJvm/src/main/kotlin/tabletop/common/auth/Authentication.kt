package tabletop.common.auth

import arrow.core.Either
import tabletop.common.error.CommonError
import tabletop.common.user.User
import java.io.Serializable


abstract class Authentication {
    abstract suspend fun authenticate(principal: String, secret: String): Either<Error, User>

    class Error(override val message: String?, override val cause: CommonError?) :
        CommonError(), Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}