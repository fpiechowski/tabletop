@file:JvmName("Lwjgl3Launcher")

package tabletop.client.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import tabletop.client.Main
import tabletop.client.event.Event
import tabletop.client.input.Input
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.command.Command
import tabletop.common.serialization.Serialization

/** Launches the desktop (LWJGL3) application. */
context (Serialization, Input, State, UserInterface, Event.Processor, Command.Processor)
fun lwjgl3Application() {
    // This handles macOS support and helps on Windows.
    if (StartupHelper.startNewJvmIfRequired())
        return
    Lwjgl3Application(Main().also { main.complete(it) }, Lwjgl3ApplicationConfiguration().apply {
        setTitle("Tabletop")
        setMaximized(true)
        setWindowIcon(*(arrayOf(128, 64, 32, 16).map { "libgdx$it.png" }.toTypedArray()))
    })
}

