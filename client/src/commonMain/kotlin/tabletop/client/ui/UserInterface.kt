package tabletop.client.ui

import korlibs.korge.view.Stage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.Serializable
import tabletop.common.error.CommonError


class UserInterface {

    val stage = CompletableDeferred<Stage>()

    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

