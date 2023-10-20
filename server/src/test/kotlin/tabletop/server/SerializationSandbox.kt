package tabletop.server

import arrow.core.raise.recover
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FreeSpec
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Game
import tabletop.common.command.Command
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.serialization.Serialization
import tabletop.common.serialization.serialize
import tabletop.server.serialization.buildSerializersModule

class SerializationSandbox : FreeSpec({
    "serialization" {
        val commandResult = GetGamesCommandResult(
            Command.GetGames(UUID.generateUUID()),
            Game.Listing(listOf(Game.Listing.Item(UUID.generateUUID(), "game", "system")))
        )

        with(Serialization { buildSerializersModule() }) {
            recover({
                val serialized = (commandResult as Command.Result<Command, Command.Result.Data>).serialize()
                println(serialized)
            }) {
                fail(it.toString())
            }
        }
    }
})