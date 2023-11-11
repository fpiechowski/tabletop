package tabletop.common.auth

import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.Entity

data class CredentialsData(
    val principal: String,
    val secret: String
)

sealed class Credentials<P, S> : Entity() {
    abstract val principal: P
    abstract val secret: S


    override fun toString(): String {
        return super.toString().replace(secret.toString(), "***", true)
    }

    data class UsernamePassword(
        val username: String,
        val password: String,
        override val id: UUID = UUID.generateUUID(),
    ) : Credentials<String, String>() {
        override val principal: String = username
        override val secret: String = password


        override fun toString(): String {
            return super.toString().replace(password, "***", true)
        }


        data class Data(
            val username: String,
            val password: String
        ) {
            companion object {
                private const val serialVersionUID = 1L
            }
        }
    }
}



