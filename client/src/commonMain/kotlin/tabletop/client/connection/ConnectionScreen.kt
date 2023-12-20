package tabletop.client.connection

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.either
import cafe.adriel.voyager.core.screen.Screen
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.event.ConnectionAttempted
import tabletop.shared.auth.Credentials
import tabletop.shared.connection.Connection
import tabletop.shared.error.CommonError
import tabletop.shared.error.ErrorHandler.Companion.use
import tabletop.shared.event.GameLoadingRequested

private val logger = KotlinLogging.logger { }

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class ConnectionScreen(
    private val dependencies: Dependencies
) : Screen {

    @Composable
    override fun Content() {
        val gamesState = dependencies.state.games
        val games by gamesState.collectAsState()

        dependencies.uiErrorHandler.errorDialog()

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (games.isEmpty()) {
                    ConnectionWindowContent()
                } else {
                    GameListing()
                }
            }
        }
    }


    @Composable
    private fun GameListing() =
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val games = remember { dependencies.state.games }
            games.value.forEach {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(it.name, style = MaterialTheme.typography.titleMedium)
                        Button(onClick = {
                            dependencies.run {
                                with(eventHandler) {
                                    launch {
                                        uiErrorHandler.use {
                                            GameLoadingRequested(it.id).handle().bind()
                                        }
                                    }
                                }
                            }
                        }) {
                            Text("Load Game")
                        }
                    }
                }
            }
        }

    @Composable
    private fun ConnectionWindowContent() =
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
                                            uiErrorHandler.use {
                                                val (host, port) = parseServerUrl(host.value)

                                                ConnectionAttempted(
                                                    host = host,
                                                    port = port,
                                                    credentialsData = Credentials.UsernamePassword.Data(
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

    private fun Raise<Connection.Error>.parseServerUrl(serverUrl: String) = catch({
        serverUrl.split(":", limit = 2).let { it[0] to it[1].toInt() }
    }) { raise(Connection.Error("Can't parse server URL", CommonError.ThrowableError(it))) }
}

