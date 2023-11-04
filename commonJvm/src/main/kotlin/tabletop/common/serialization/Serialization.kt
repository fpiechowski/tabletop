package tabletop.common.serialization

import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tabletop.common.auth.Authentication
import tabletop.common.command.Command
import tabletop.common.command.GetGameCommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.command.SignInCommandResult
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.game.Game
import tabletop.common.server.Server
import tabletop.common.user.User


class Serialization {
    val json: Json = Json {
        ignoreUnknownKeys = true

        this.serializersModule = SerializersModule {
            buildCommonSerializersModule()
        }
    }

    inline fun <reified T> T.serialize(): Either<Error, String> = either {
        catch({
            json.encodeToString<T>(this@serialize)
        }) {
            raise(Error("Can't encode to string", CommonError.ThrowableError(it)))
        }
    }

    inline fun <reified T> String.deserialize(): Either<Error, T> = either {
        catch({
            json.decodeFromString<T>(this@deserialize)
        }) {
            raise(Error("Can't decode from string ${this@deserialize}", CommonError.ThrowableError(it)))
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

    @Serializable
    class Error(override val message: String?, override val cause: CommonError?) : CommonError()
}
