package tabletop.auth

import kotlinx.serialization.Serializable

@Serializable
sealed class Credentials<P, S> {
    abstract val principal: P
    abstract val secret: S

    @Serializable
    class UsernamePassword(
        val username: String,
        val password: String
    ) : Credentials<String, String>() {
        override val principal: String get() = username
        override val secret: String get() = password
    }
}
