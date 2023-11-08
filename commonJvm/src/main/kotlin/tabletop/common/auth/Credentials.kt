package tabletop.common.auth

import tabletop.common.Entity
import java.io.Serializable
import java.util.*

data class CredentialsData(
    val principal: String,
    val secret: String
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

sealed class Credentials<P, S> : Entity(), Serializable {
    abstract val principal: P
    abstract val secret: S

    companion object {
        private const val serialVersionUID = 1L
    }
    override fun toString(): String {
        return super.toString().replace(secret.toString(), "***", true)
    }

    data class UsernamePassword(
        val username: String,
        val password: String,
        override val id: UUID = UUID.randomUUID(),
    ) : Credentials<String, String>(), Serializable {
        override val principal: String = username
        override val secret: String = password

        companion object {
            private const val serialVersionUID = 1L
        }
        override fun toString(): String {
            return super.toString().replace(password, "***", true)
        }


        data class Data(
            val username: String,
            val password: String
        ) : Serializable {
            companion object {
                private const val serialVersionUID = 1L
            }
        }
    }
}



