package tabletop.shared.demo

import kotlinx.uuid.UUID
import tabletop.shared.auth.Credentials
import tabletop.shared.dnd5e.DnD5e
import tabletop.shared.dnd5e.character.*
import tabletop.shared.game.Game
import tabletop.shared.game.player.Player
import tabletop.shared.scene.Scene
import tabletop.shared.user.GameMaster
import tabletop.shared.user.User


val demoGmUser = User(
    name = "Demo GM User",
    id = UUID("eb946023-ba3f-48c2-90e3-f778aab1079d")
)

val demoGmUserCredentials = Credentials.UsernamePassword("gm", "gm")

val demoPlayerUser = User(
    name = "Demo Player User",
    id = UUID("d01cae9b-c89f-46fa-9492-fad1af1ead82")
)

val demoPlayerUserCredentials = Credentials.UsernamePassword("player", "player")

val demoGame = UUID("8b6c9a25-066d-4233-8f26-0cb4c141b7af").let { gameId ->
    Game(
        name = "demo2",
        id = gameId,
        system = DnD5e(),
        gameMaster = GameMaster("gm", gameId, demoGmUser)
    )
}


val demoGame2 = UUID("e84e8dca-b4f8-48c4-b45a-a0bad8a0d4a0").let { gameId ->
    Game(
        name = "demo2",
        id = gameId,
        system = DnD5e(),
        gameMaster = GameMaster("gm", gameId, demoGmUser)
    )
}


val demoScene = Scene(
    name = "Demo Scene",
    gameId = demoGame.id,
    foregroundImagePath = "assets/demo/sceneImage.png",
    id = UUID("815a027a-dfa7-44f2-83aa-a8073bd5b5bb")
)

val demoNonPlayerCharacter = Character(
    hp = 10,
    race = Human(),
    characterClassesLevels = setOf(),
    skillProficiencies = setOf(),
    attributes = Character.Attributes(),
    equipment = Character.Equipment(),
    name = "Demo NPC",
    tokenImageFilePath = "assets/demo/tokenImage.png",
    currentHp = 10,
    id = UUID("9186fb5e-9387-4527-a02c-15f5dabd3932")
)

val demoCharacter = Character(
    hp = 10,
    race = Human(),
    characterClassesLevels = setOf(Character.CharacterClassLevel(1, CharacterClass.figter)),
    attributes = Character.Attributes(),
    savingThrowProficiencies = setOf(),
    skillProficiencies = setOf(),
    name = "Demo PC",
    tokenImageFilePath = "assets/demo/tokenImage.png",
    id = UUID("b09d86a1-a45e-414a-8428-56914d7b9cba"),
    currentHp = 10,
    player = Player("Demo Player", demoGame.id, demoPlayerUser)
)


