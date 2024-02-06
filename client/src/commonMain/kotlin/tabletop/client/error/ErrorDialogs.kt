package tabletop.client.error

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.update
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import tabletop.client.di.Dependencies
import tabletop.shared.error.CommonError
import tabletop.shared.error.ErrorHandler


@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class ErrorDialogs(
    private val dependencies: Dependencies
) : ErrorHandler<CommonError> {

    val errors: MutableValue<List<CommonError>> = MutableValue(listOf())



    override suspend fun CommonError.handle() {
        with(dependencies.terminalErrorHandler) { handle() }

        errors.update {
            it + this@handle
        }
    }

    @Composable
    fun errorDialog() {
        val errors = errors.subscribeAsState()

        return errors.value.firstOrNull()?.let {
            AlertDialog(
                onDismissRequest = { this@ErrorDialogs.errors.value -= it },
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
                    Button(onClick = { this@ErrorDialogs.errors.value -= it }) {
                        Text("OK")
                    }
                }
            )
        } ?: Box(Modifier)
    }
}

