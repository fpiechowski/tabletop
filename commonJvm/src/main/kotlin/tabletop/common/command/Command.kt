package tabletop.common.command

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.common.error.CommonError
import tabletop.common.process.CommonChannel

typealias CommandResult = Command.Result<Command, Command.Result.Data>

@Serializable
sealed class Command {

    @Serializable
    data class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()

    companion object

    class Channel : CommonChannel<Command>() {
        companion object

        override

        override val logger: KLogger = KotlinLogging.logger {}
    }

    @Serializable
    data class SignIn(
        val principal: String,
        val secret: String
    ) : Command()

    @Serializable
    data class GetGames(
        val userId: UUID
    ) : Command()

    @Serializable
    data class GetGame(
        val gameId: UUID
    ) : Command()

    @Serializable
    abstract class Result<C : Command, T : Result.Data> {
        abstract val command: C
        abstract val data: T
        abstract val shared: Boolean


        @Serializable
        class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()

        companion object

        interface Data

        class Channel : CommonChannel<Result<*, *>>() {
            override val logger: KLogger = KotlinLogging.logger {}

            companion object
        }
    }
}
