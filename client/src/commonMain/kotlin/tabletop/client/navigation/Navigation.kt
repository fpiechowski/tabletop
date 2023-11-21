package tabletop.client.navigation

import dev.fritz2.routing.Route
import dev.fritz2.routing.Router
import dev.fritz2.routing.routerOf
import tabletop.client.ui.UserInterface

class Navigation(
    private val userInterface: UserInterface
) {
    private val router: Router<String> = routerOf("connection")

    fun navigate(route: Route) =
        router.navTo(route.name.lowercase())

    enum class Route {
        Connection,
        Game,
        Debug
    }
}