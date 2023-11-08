package tabletop.common.server

import tabletop.common.error.CommonError

abstract class Server {
    companion object

    class Error(override val message: String?, override val cause: CommonError?) : CommonError(), java.io.Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}
