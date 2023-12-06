package tabletop.common.game.player

import tabletop.common.user.User


abstract class PlayerEntity {
    abstract val Player.user: User
}