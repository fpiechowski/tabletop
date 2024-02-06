import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.badoo.reaktive.coroutinesinterop.asScheduler
import com.badoo.reaktive.scheduler.overrideSchedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.client.di.Dependencies
import tabletop.client.dnd5e.character.CharacterSheet
import tabletop.shared.ApplicationInfo
import tabletop.shared.demo.demoCharacter
import javax.swing.SwingUtilities


@ExperimentalDecomposeApi
@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
fun main() {
    overrideSchedulers(main = Dispatchers.Main::asScheduler)

    val lifecycle = LifecycleRegistry()

    val dependencies =
        runOnUiThread {
            Dependencies(
                context = DefaultComponentContext(lifecycle = lifecycle),
            )
        }

    application {
        val windowState = rememberWindowState()

        LifecycleController(lifecycle, windowState)

        MaterialTheme {
            Window(onCloseRequest = ::exitApplication, title = ApplicationInfo.Name, state = windowState) {
                tabletop.client.application(dependencies)
            }
        }
    }
}

fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    var error: Throwable? = null
    var result: T? = null

    SwingUtilities.invokeAndWait {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }

    error?.also { throw it }

    @Suppress("UNCHECKED_CAST")
    return result as T
}