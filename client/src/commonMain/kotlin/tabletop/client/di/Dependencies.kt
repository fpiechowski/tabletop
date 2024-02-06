package tabletop.client.di

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.lifecycle.Lifecycle
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CompletableDeferred
import tabletop.client.asset.Assets
import tabletop.client.error.ErrorDialogs
import tabletop.client.event.EventHandler
import tabletop.client.navigation.Navigation
import tabletop.client.state.State
import tabletop.client.ui.Windows
import tabletop.shared.connection.Connection
import tabletop.shared.connection.ConnectionCommunicator
import tabletop.shared.di.CommonDependencies
import tabletop.shared.error.CommonError
import tabletop.shared.error.ConnectionErrorHandler
import tabletop.shared.error.TerminalErrorHandler
import tabletop.shared.serialization.Serialization


@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class Dependencies(
    context: ComponentContext
) : CommonDependencies, ComponentContext by context {
    private val logger = KotlinLogging.logger { }

    override val serialization: Serialization = Serialization()
    override val terminalErrorHandler: TerminalErrorHandler = TerminalErrorHandler()
    val windows: Windows = Windows(childDependencies("windows"))
    val navigation: Navigation = Navigation(childDependencies("navigation"))
    val state: State = State()
    val errorDialogs: ErrorDialogs = ErrorDialogs(this)
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

    fun childDependencies(key: String, lifecycle: Lifecycle? = null): Dependencies =
        Dependencies(
            context = childContext(key = key, lifecycle = lifecycle)
        )


    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object {
        val instance: CompletableDeferred<Dependencies> = CompletableDeferred()
    }
}

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class ConnectionDependencies(
    val dependencies: Dependencies,
    override val connection: Connection
) : CommonDependencies.ConnectionScope{


    val assets: Assets = Assets(this)

    override val connectionCommunicator: ConnectionCommunicator =
        ConnectionCommunicator(connection, dependencies.serialization)

    override val connectionErrorHandler: ConnectionErrorHandler =
        ConnectionErrorHandler(dependencies.terminalErrorHandler, connectionCommunicator)

    fun interface Factory {
        operator fun invoke(connection: Connection): ConnectionDependencies
    }
}



