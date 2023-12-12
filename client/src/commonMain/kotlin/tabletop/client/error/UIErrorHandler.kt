package tabletop.client.error

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import tabletop.client.di.Dependencies
import tabletop.common.error.CommonError
import tabletop.common.error.ErrorHandler
import kotlin.coroutines.CoroutineContext


@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class UIErrorHandler(
    private val dependencies: Dependencies
) : ErrorHandler<CommonError>, CoroutineScope {

    private val logger = KotlinLogging.logger { }

    override suspend fun CommonError.handle() {
        with(dependencies.terminalErrorHandler) { handle() }

        dependencies.state.errors.update {
            it + this@handle
        }
    }

    @Composable
    fun errorDialog() = with(dependencies) {
        val errors by state.errors.collectAsState()

        errors.firstOrNull()?.let {
            AlertDialog(
                onDismissRequest = { state.errors.value -= it },
                title = { Text("Error") },
                text = {
                    TextField(
                        value = it.toString(),
                        onValueChange = {},
                        enabled = true,
                        minLines = 5,
                        maxLines = 5,
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                    )
                },
                confirmButton = {
                    Button(onClick = { state.errors.value -= it }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    private val job = Job()
    override val coroutineContext: CoroutineContext = job
}

