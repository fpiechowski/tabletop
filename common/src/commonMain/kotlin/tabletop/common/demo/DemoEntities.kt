package tabletop.common.demo

import kotlinx.uuid.UUID
import tabletop.common.auth.Credentials
import tabletop.common.dnd5e.DnD5e
import tabletop.common.dnd5e.DnD5eGame
import tabletop.common.dnd5e.character.Character
import tabletop.common.dnd5e.character.Human
import tabletop.common.dnd5e.character.NonPlayerCharacter
import tabletop.common.scene.Scene
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

val demoGame = DnD5eGame(
    name = "demo",
    id = UUID("543efd85-98c2-482d-a292-6c9b2c188b7a"),
    system = DnD5e(),
    initialGameMasterUser = demoGmUser
)


val demoGame2 = DnD5eGame(
    name = "demo2",
    id = UUID("8b6c9a25-066d-4233-8f26-0cb4c141b7af"),
    system = DnD5e(),
    initialGameMasterUser = demoGmUser
)


val demoScene = Scene(
    "Demo Scene",
    demoGame.id,
    "assets/demo/sceneImage.png",
    id = UUID("815a027a-dfa7-44f2-83aa-a8073bd5b5bb")
)

val demoNonPlayerCharacter = NonPlayerCharacter(
    10,
    Human(),
    setOf(),
    setOf(),
    Character.Attributes(),
    setOf(),
    "Demo",
    "assets/demo/tokenImage.png",
    UUID("9186fb5e-9387-4527-a02c-15f5dabd3932")
)

