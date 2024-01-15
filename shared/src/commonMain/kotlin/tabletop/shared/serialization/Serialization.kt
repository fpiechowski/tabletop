package tabletop.shared.serialization

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tabletop.shared.auth.Authentication
import tabletop.shared.connection.Connection
import tabletop.shared.dnd5e.DnD5e
import tabletop.shared.dnd5e.character.*
import tabletop.shared.error.*
import tabletop.shared.event.Event
import tabletop.shared.game.Game
import tabletop.shared.persistence.Persistence
import tabletop.shared.server.Server
import tabletop.shared.system.System


class Serialization {

    val json: Json = Json {
        serializersModule = SerializersModule {
            polymorphic(CommonError::class) {
                subclass(Error::class)
                subclass(Event.Error::class)
                subclass(Connection.Error::class)
                subclass(Server.Error::class)
                subclass(Authentication.Error::class)
                subclass(CommonError.ThrowableError::class)
                subclass(NotFoundError::class)
                subclass(NotImplementedError::class)
                subclass(IllegalStateError::class)
                subclass(InvalidSubtypeError::class)
                subclass(Persistence.Error::class)
            }

            polymorphic(Race::class) {
                subclass(Human::class)
            }

            polymorphic(System::class) {
                subclass(DnD5e::class)
            }
        }
    }

    inline fun <reified T> T.serialize(): Either<Error, String> = either {
        catch({
            json.encodeToString<T>(this@serialize)
        }) {
            raise(Error("Can't serialize ${T::class}", CommonError.ThrowableError(it)))
        }
    }

    inline fun <reified T> String.deserialize(): Either<Error, T> = either {
        catch({
            json.decodeFromString<T>(this@deserialize)
        }) {
            raise(Error("Can't deserialize to ${T::class}", CommonError.ThrowableError(it)))
        }
    }

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}