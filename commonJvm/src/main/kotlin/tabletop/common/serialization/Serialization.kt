package tabletop.common.serialization

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import org.nustaq.serialization.FSTConfiguration
import tabletop.common.error.CommonError


class Serialization {
    val fst: FSTConfiguration = FSTConfiguration.createDefaultConfiguration()

    inline fun <reified T> T.serialize(): Either<Error, ByteArray> = either {
        catch({
            fst.asByteArray(this@serialize)
        }) {
            raise(Error("Can't serialize ${T::class}", CommonError.ThrowableError(it)))
        }
    }

    inline fun <reified T> ByteArray.deserialize(): Either<Error, T> = either {
        catch({
            fst.asObject(this@deserialize) as T
        }) {
            raise(Error("Can't deserialize to ${T::class}", CommonError.ThrowableError(it)))
        }
    }


    class Error(override val message: String?, override val cause: CommonError?) : CommonError(), java.io.Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}