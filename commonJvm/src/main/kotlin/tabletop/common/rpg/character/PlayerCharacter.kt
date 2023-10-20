package tabletop.common.rpg.character

import tabletop.common.user.Player

class PlayerCharacter(
    override val name: String,
    val player: Player
) : Character(name)
