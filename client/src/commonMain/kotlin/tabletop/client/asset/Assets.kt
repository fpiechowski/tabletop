package tabletop.client.asset

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensureNotNull
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.flow.update
import tabletop.client.di.ConnectionDependencies
import tabletop.client.di.Dependencies
import tabletop.client.server.Server
import tabletop.shared.error.CommonError
import java.io.File

@ExperimentalLayoutApi
@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
class Assets(
    private val connectionDependencies: ConnectionDependencies
) {
    private val logger = KotlinLogging.logger { }


    data class Download(val path: String, val file: File, val downloaded: Long, val total: Long)

    data class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    private val httpClient = HttpClient()

    private fun serverUrl(path: String): Url = URLBuilder(
        protocol = URLProtocol.HTTP,
        host = connectionDependencies.connection.host,
        port = connectionDependencies.connection.port,
        pathSegments = path.split("/")
    ).build()

    suspend fun assetFile(path: String): Either<Error, File> = either {
        File(path).also { logger.debug { it.absolutePath } }
            .also { file ->
                if (!file.exists()) {
                    file.parentFile.mkdirs()
                    logger.debug { "Downloading asset $path" }
                    Server.httpClient.prepareGet {
                        url(serverUrl(path))
                    }.execute { response ->
                        val channel: ByteReadChannel = response.body()
                        val totalLength = ensureNotNull(response.contentLength()) {
                            Error(
                                "Content length of downloaded file is null",
                                null
                            )
                        }
                        while (!channel.isClosedForRead) {
                            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                            while (!packet.isEmpty) {
                                val bytes = packet.readBytes()
                                file.appendBytes(bytes)
                                logger.debug { "Received ${file.length()} bytes from $totalLength" }
                                connectionDependencies.dependencies.state.assetDownloads.update {
                                    it + (path to Download(path, file, file.length(), totalLength))
                                }
                            }
                        }
                    }
                }
            }
    }
}