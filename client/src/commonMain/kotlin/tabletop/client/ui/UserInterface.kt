package tabletop.client.ui

import korlibs.korge.view.Stage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.Serializable
import tabletop.client.connection.ConnectionScene
import tabletop.common.error.CommonError


class UserInterface {

    val stage = CompletableDeferred<Stage>()
    val connectionScene = ConnectionScene()

    companion object

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}

