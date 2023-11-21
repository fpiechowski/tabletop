package tabletop.common.server

import kotlinx.serialization.Serializable
import tabletop.common.error.CommonError

abstract class Server {
    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}
