package tabletop.client.navigation

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.Stack
import cafe.adriel.voyager.navigator.Navigator
import tabletop.client.ui.UserInterface

class Navigation(
    private val userInterface: UserInterface,
    private val navigator: Navigator
) : Stack<Screen> by navigator {


    enum class Route {
        Connection,
        Game,
        Debug
    }
}