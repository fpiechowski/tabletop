package tabletop.common.demo

import kotlinx.uuid.UUID
import tabletop.common.Game
import tabletop.common.auth.Credentials
import tabletop.common.dnd5e.DnD5e
import tabletop.common.user.GameMaster
import tabletop.common.user.Player
import tabletop.common.user.User


val demoGmUser = User(
    "Demo GM User",
    id = UUID("eb946023-ba3f-48c2-90e3-f778aab1079d")
)

val demoGmUserCredentials = Credentials.UsernamePassword("gm", "gm")

val demoPlayerUser = User(
    "Demo Player User",
    id = UUID("d01cae9b-c89f-46fa-9492-fad1af1ead82")
)

val demoPlayerUserCredentials = Credentials.UsernamePassword("player", "player")

val demoGame = Game(
    "demo",
    DnD5e,
    GameMaster("gm", demoGmUser),
    setOf(Player("player", demoPlayerUser)),
    id = UUID("543efd85-98c2-482d-a292-6c9b2c188b7a")
)

val demoGame2 = Game(
    "demo2",
    DnD5e,
    GameMaster("gm", demoGmUser),
    setOf(Player("player", demoPlayerUser)),
    id = UUID("8b6c9a25-066d-4233-8f26-0cb4c141b7af")
)