package tabletop.common.auth

import kotlinx.serialization.Serializable
import kotlinx.uuid.UUID
import kotlinx.uuid.generateUUID
import tabletop.common.entity.Entity
import tabletop.common.entity.Identifiable

sealed class Credentials<P, S> : Identifiable<UUID> {
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


        @Serializable
        data class Data(
            val username: String,
            val password: String
        )
    }
}



