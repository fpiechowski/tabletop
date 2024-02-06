package tabletop.client.connection

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import arrow.core.raise.Raise
import arrow.core.raise.either
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.ConnectionAttempted
import tabletop.shared.connection.Connection
import tabletop.shared.error.CommonError
import tabletop.shared.error.ErrorHandler.Companion.use

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@ExperimentalLayoutApi
class ConnectionWindow(
    val dependencies: Dependencies
) {
    private val logger = KotlinLogging.logger {}

    @Composable
    fun content() {
        fun Raise<Connection.Error>.parseServerUrl(serverUrl: String) = arrow.core.raise.catch({
            serverUrl.split(":", limit = 2).let { it[0] to it[1].toInt() }
        }) { raise(Connection.Error("Can't parse server URL", CommonError.ThrowableError(it))) }

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Connection",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                )
                val host = remember { mutableStateOf("localhost:8080") }
                val username = remember { mutableStateOf("gm") }
                val password = remember { mutableStateOf("gm") }

                Column(modifier = Modifier.padding(8.dp)) {
                    TextField(
                        label = { Text("Host") },
                        value = host.value,
                        onValueChange = { host.value = it }
                    )
                    TextField(
                        label = { Text("Username") },
                        value = username.value,
                        onValueChange = { username.value = it }
                    )
                    TextField(
                        label = { Text("Password") },
                        value = password.value,
                        onValueChange = { password.value = it },
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            logger.debug { "connectionButton.onClick" }
                            dependencies.run {
                                logger.debug { eventHandler.toString() }
                                with(eventHandler) {
                                    either<CommonError, Any> {
                                        launch {
                                            errorDialogs.use {
                                                val (host, port) = parseServerUrl(host.value)

                                                ConnectionAttempted(
                                                    host = host,
                                                    port = port,
                                                    credentialsData = tabletop.shared.auth.Credentials.UsernamePassword.Data(
                                                        username.value,
                                                        password.value
                                                    )
                                                ).handle().bind()
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                        Text("Connect")
                    }
                }
            }
        }
    }
}