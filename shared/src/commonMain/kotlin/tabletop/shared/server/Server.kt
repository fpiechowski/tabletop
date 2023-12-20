package tabletop.shared.server

import kotlinx.serialization.Serializable
import tabletop.shared.error.CommonError

abstract class Server {
    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}
