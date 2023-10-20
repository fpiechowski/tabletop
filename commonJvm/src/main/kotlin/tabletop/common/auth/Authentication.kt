package tabletop.common.auth

import kotlinx.serialization.Serializable
import tabletop.common.error.CommonError


object Authentication {
    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) :
        CommonError()
}