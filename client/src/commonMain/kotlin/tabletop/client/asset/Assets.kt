package tabletop.client.asset

import androidx.compose.ui.ExperimentalComposeUiApi
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import tabletop.client.di.ConnectionDependencies
import tabletop.client.server.ServerAdapter
import java.io.File

@ExperimentalComposeUiApi
class Assets(
    val dependencies: ConnectionDependencies
) {
    private val logger = KotlinLogging.logger { }

    private val httpClient = HttpClient()
    private fun serverUrl(path: String): Url = URLBuilder(
        protocol = URLProtocol.HTTP,
        host = dependencies.connection.host,
        port = dependencies.connection.port,
        pathSegments = path.split("/")
    ).build()

    suspend fun assetFile(path: String): File = File(path).also { logger.debug { it.absolutePath } }
        .also {
            if (!it.exists()) {
                ServerAdapter.httpClient.prepareGet {
                    url(serverUrl(path))
                }.execute { response ->
                    val channel: ByteReadChannel = response.body()
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                        while (!packet.isEmpty) {
                            val bytes = packet.readBytes()
                            it.appendBytes(bytes)
                            logger.debug { "Received ${it.length()} bytes from ${response.contentLength()}" }
                        }
                    }
                }
            }
        }
}