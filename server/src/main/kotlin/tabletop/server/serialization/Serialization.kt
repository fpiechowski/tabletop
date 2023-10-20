package tabletop.server.serialization

import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tabletop.common.auth.Authentication
import tabletop.common.command.Command
import tabletop.common.error.CommonError
import tabletop.common.server.Server
import tabletop.server.persistence.Persistence

fun SerializersModuleBuilder.buildSerializersModule() {
    polymorphic(CommonError::class) {
        subclass(Authentication.Error::class)
        subclass(Persistence.Error::class)
        subclass(Command.Error::class)
        subclass(Server.Error::class)
    }
}
