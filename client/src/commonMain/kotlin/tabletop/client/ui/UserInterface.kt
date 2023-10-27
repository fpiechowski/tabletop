package tabletop.client.ui

import kotlinx.serialization.Serializable
import tabletop.common.error.CommonError


class UserInterface {

    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}

