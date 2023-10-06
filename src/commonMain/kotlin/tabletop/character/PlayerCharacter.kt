package tabletop.character

import tabletop.user.Player

class PlayerCharacter(
    override val name: String,
    val player: Player
) : Character(name)
