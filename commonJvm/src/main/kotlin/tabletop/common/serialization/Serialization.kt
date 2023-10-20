package tabletop.common.serialization

import arrow.core.raise.Raise
import arrow.core.raise.catch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tabletop.common.Game
import tabletop.common.auth.Authentication
import tabletop.common.command.Command
import tabletop.common.command.GetGameCommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.command.SignInCommandResult
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.server.Server
import tabletop.common.user.User


class Serialization(serializersModuleBuilder: SerializersModuleBuilder.() -> Unit) {
    val json: Json = Json {
        ignoreUnknownKeys = true

        this.serializersModule = SerializersModule {
            buildCommonSerializersModule()
            serializersModuleBuilder()
        }
    }

    companion object {
        fun SerializersModuleBuilder.buildCommonSerializersModule() {
            polymorphic(CommonError::class) {
                subclass(Error::class)
                subclass(Authentication.Error::class)
                subclass(Command.Error::class)
                subclass(Command.Result.Error::class)
                subclass(Server.Error::class)
                subclass(Connection.Error::class)
            }

            polymorphic(Command.Result::class) {
                subclass(GetGamesCommandResult::class)
                subclass(GetGameCommandResult::class)
                subclass(SignInCommandResult::class)
            }

            polymorphic(Command.Result.Data::class) {
                subclass(Game::class)
                subclass(Game.Listing::class)
                subclass(User::class)
            }
        }
    }

    @kotlinx.serialization.Serializable
    class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()
}

context (Raise<Serialization.Error>, Serialization)
inline fun <reified T : Any> T.serialize(): String = catch({
    json.encodeToString<T>(this)
}) {
    raise(Serialization.Error("Can't encode to string", CommonError.ThrowableError(it)))
}

context (Raise<Serialization.Error>, Serialization)
fun <T : Any> Serializable<T>.serialize(): String = catch({
    json.encodeToString(serializer, this.value)
}) {
    raise(Serialization.Error("Can't encode to string", CommonError.ThrowableError(it)))
}

context (Raise<Serialization.Error>, Serialization)
inline fun <reified T : Any> deserialize(string: String): T = catch({
    json.decodeFromString(string)
}) {
    raise(Serialization.Error("Can't decode from string $string", CommonError.ThrowableError(it)))
}

context (Raise<Serialization.Error>, Serialization)
fun <T : Any> deserialize(serializer: KSerializer<T>, string: String): T = catch({
    json.decodeFromString(serializer, string)
}) {
    raise(Serialization.Error("Can't decode from string $string", CommonError.ThrowableError(it)))
}