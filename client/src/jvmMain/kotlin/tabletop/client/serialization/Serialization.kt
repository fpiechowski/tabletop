package tabletop.client.serialization

import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError

fun SerializersModuleBuilder.buildSerializersModule() {
    polymorphic(CommonError::class) {
        subclass(UserInterface.Error::class)
    }
}
