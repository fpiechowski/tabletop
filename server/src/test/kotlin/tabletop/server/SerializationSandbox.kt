package tabletop.server

import arrow.core.raise.recover
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FreeSpec
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Game
import tabletop.common.command.Command
import tabletop.common.command.CommandResult
import tabletop.common.command.GetGamesCommandResult
import tabletop.common.serialization.Serialization

class SerializationSandbox : FreeSpec({
    "serialization" {
        val commandResult = GetGamesCommandResult(
            Command.GetGames(UUID.generateUUID()),
            Game.Listing(listOf(Game.Listing.Item(UUID.generateUUID(), "game", "system")))
        )

        with(Serialization()) {
            recover({
                @Suppress("UNCHECKED_CAST") val serialized =
                    (commandResult as CommandResult).serialize().bind()
                println(serialized)
            }) {
                fail(it.toString())
            }
        }
    }
})