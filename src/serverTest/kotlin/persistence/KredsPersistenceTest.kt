package persistence

import arrow.core.raise.either
import com.redis.testcontainers.RedisStackContainer
import io.github.crackthecodeabhi.kreds.connection.Endpoint
import io.github.crackthecodeabhi.kreds.connection.newClient
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.extensions.install
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.testcontainers.ContainerExtension
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.serialization.json.Json
import tabletop.Game
import tabletop.System
import tabletop.character.Character
import tabletop.user.GameMaster
import tabletop.user.User

class KredsPersistenceTest : FreeSpec({

    val redisContainer = install(
        ContainerExtension(
            RedisStackContainer(RedisStackContainer.DEFAULT_IMAGE_NAME)
        )
    ) {
        withExposedPorts(6379)
    }

    val persistence = Persistence(
        newClient(Endpoint.from("localhost:${redisContainer.firstMappedPort}")),
        Json
    )

    "persist" - {
        "character" {
            val character = Character("character")

            either {
                with(persistence) {
                    character.persist()
                }
            }.shouldBeRight()
        }
    }

    "retrieve" - {
        "character" {
            val character = Character("character")
            val game = Game("game", TestSystem, GameMaster("gm", User("user")))

            either {
                with(persistence) {
                    game.persist()
                    retrieve<Character>(game.id)
                }
            }.shouldBeRight()
                .shouldNotBeNull()
                .shouldBeEqualToComparingFields(character)
        }
    }
}) {
    object TestSystem : System("test")
}
