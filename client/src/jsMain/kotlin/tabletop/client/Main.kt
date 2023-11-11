package tabletop.client

import dev.fritz2.core.render
import tabletop.client.di.Dependencies

fun main() {
    val dependencies = Dependencies().also { Dependencies.instance.complete(it) }
    render {
        dependencies.userInterface.connectionScene

        p { }
    }
}

