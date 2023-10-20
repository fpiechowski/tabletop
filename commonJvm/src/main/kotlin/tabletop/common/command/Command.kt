package tabletop.common.command

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import tabletop.common.error.CommonError
import tabletop.common.process.ChannelProcessor

@Serializable
sealed class Command {

    @Serializable
    data class Error(override val message: String?, override val cause: CommonError? = null) : CommonError()

    companion object

    class Processor : ChannelProcessor<Command>() {
        companion object
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

        class Processor : ChannelProcessor<Result<*, *>>() {
            companion object
        }
    }
}
