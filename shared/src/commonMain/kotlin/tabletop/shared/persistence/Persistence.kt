package tabletop.shared.persistence

import arrow.core.Either
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import tabletop.shared.error.CommonError
import tabletop.shared.hex.Adapter
import tabletop.shared.hex.Port
import kotlin.coroutines.CoroutineContext

interface Persistence : Port, CoroutineScope {

    @Serializable
    data class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}