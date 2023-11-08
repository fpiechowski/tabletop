package tabletop.common.event

import tabletop.common.auth.Credentials
import tabletop.common.game.Game
import tabletop.common.geometry.Point
import tabletop.common.scene.token.Token
import tabletop.common.user.User
import java.io.Serializable
import java.util.*


data class AuthenticationRequested(
    val credentialsData: Credentials.UsernamePassword.Data
) :
    RequestEvent, Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

data class UserAuthenticated(val user: User) : ResultEvent, Serializable {
    override val shared: Boolean = false

    companion object {
        private const val serialVersionUID = 1L
    }
}

data class GameListingRequested(val userId: UUID) : RequestEvent, Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

data class GameListingLoaded(val listing: Game.Listing) : ResultEvent, Serializable {
    override val shared: Boolean = false

    companion object {
        private const val serialVersionUID = 1L
    }
}

data class LoadingGameRequested(val gameListingItem: Game.Listing.Item) : RequestEvent, Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

data class GameLoaded(val game: Game<*>) : ResultEvent, Serializable {
    override val shared: Boolean = false

    companion object {
        private const val serialVersionUID = 1L
    }
}

data class SceneOpeningRequested(val sceneId: UUID) : RequestEvent, Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

data class SceneOpened(val sceneId: UUID) : ResultEvent, Serializable {
    override val shared: Boolean = false

    companion object {
        private const val serialVersionUID = 1L
    }
}

data class TokenPlacingRequested(
    val gameId: UUID,
    val tokenizableId: UUID,
    val sceneId: UUID,
    val position: Point
) : RequestEvent, Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

data class TokenPlaced(val token: Token<*>) : ResultEvent, Serializable {
    override val shared: Boolean = false

    companion object {
        private const val serialVersionUID = 1L
    }
}

