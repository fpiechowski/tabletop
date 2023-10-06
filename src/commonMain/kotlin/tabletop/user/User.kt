package tabletop.user

import kotlinx.serialization.Serializable
import tabletop.Entity
import tabletop.auth.Credentials

@Serializable
class User(
    override val name: String,
    val credentials: Credentials<String, String>
) : Entity() {
    interface Role
}
