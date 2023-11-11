package tabletop.client.assets

import arrow.core.raise.catch
import arrow.core.raise.either
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import korlibs.io.file.std.applicationVfs
import korlibs.io.stream.toAsync
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError

class AssetStorage(val connection: Connection) {

    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    private val vfs = applicationVfs

    private val httpClient: HttpClient = HttpClient {
        defaultRequest {
            url {
                host = connection.host
                port = connection.port
                path("assets/")
            }
        }
    }

    suspend fun get(path: String) = either {
        catch({ vfs[path].takeIfExists() ?: fetch(path).bind() }) {
            raise(Error("Can't get asset for path $path", CommonError.ThrowableError(it)))
        }
    }

    suspend fun fetch(path: String) = either {
        catch({
            applicationVfs.get("assets/${path.trim('/')}")
                .writeStream(httpClient.get(path).bodyAsChannel().toInputStream().toAsync())

            vfs[path]
        }) {
            raise(Error("Can't fetch asset for path $path", CommonError.ThrowableError(it)))
        }
    }
}
