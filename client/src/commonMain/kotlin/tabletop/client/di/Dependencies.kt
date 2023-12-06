package tabletop.client.di

import androidx.compose.ui.ExperimentalComposeUiApi
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CompletableDeferred
import tabletop.client.asset.Assets
import tabletop.client.error.UIErrorHandler
import tabletop.client.event.EventHandler
import tabletop.client.navigation.Navigation
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.connection.Connection
import tabletop.common.connection.ConnectionCommunicator
import tabletop.common.di.CommonDependencies
import tabletop.common.error.CommonError
import tabletop.common.error.ConnectionErrorHandler
import tabletop.common.error.TerminalErrorHandler
import tabletop.common.serialization.Serialization

@ExperimentalComposeUiApi
class Dependencies(
    override val serialization: Serialization = Serialization(),
    override val terminalErrorHandler: TerminalErrorHandler = TerminalErrorHandler(),
    val state: State = State(),
) : CommonDependencies {
    private val logger = KotlinLogging.logger {  }

    val navigation: CompletableDeferred<Navigation> = CompletableDeferred()
    val userInterface: UserInterface = UserInterface(this)
    val uiErrorHandler: UIErrorHandler = UIErrorHandler(userInterface, terminalErrorHandler)
    val eventHandler: EventHandler = EventHandler(this)
    val connectionDependenciesFactory = ConnectionDependencies.Factory { connection ->
        ConnectionDependencies(this, connection)
            .also { state.connectionDependencies.value = it }
    }

    init {
        if (!instance.isCompleted) {
            instance.complete(this)
        } else {
            with(terminalErrorHandler) {
                Error("${Dependencies::class.simpleName} instance already completed", null)
                    .handleSync()
            }
        }
    }





    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object {
        val instance: CompletableDeferred<Dependencies> = CompletableDeferred()
    }
}

@ExperimentalComposeUiApi
class ConnectionDependencies(
    dependencies: Dependencies,
    override val connection: Connection,
    override val connectionCommunicator: ConnectionCommunicator =
        ConnectionCommunicator(connection, dependencies.serialization),
    override val connectionErrorHandler: ConnectionErrorHandler = ConnectionErrorHandler(
        dependencies.terminalErrorHandler,
        connectionCommunicator
    ),
) : CommonDependencies.ConnectionScope {

    val assets: Assets = Assets(this)

    fun interface Factory {
        operator fun invoke(connection: Connection): ConnectionDependencies
    }
}



