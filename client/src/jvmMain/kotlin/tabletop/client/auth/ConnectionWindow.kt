package tabletop.client.auth

import arrow.core.raise.recover
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.async.KtxAsync
import ktx.scene2d.scene2d
import ktx.scene2d.vis.*
import tabletop.client.error.handleUI
import tabletop.client.event.ConnectionAttempted
import tabletop.client.event.Event
import tabletop.client.input.Input
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.auth.Credentials
import tabletop.common.connection.Connection
import tabletop.common.publish
import tabletop.common.serialization.Serialization


context (Input, Serialization, UserInterface, Event.Processor, State)
fun connectionWindow() =
    scene2d.visWindow("Login") {
        val window = this
        window.isMovable = false
        val hostnameTextField = visTextField("localhost:8080")
        val usernameTextField = visTextField()
        val passwordTextField = visTextField()

        visTable(true) {
            pad(10f)
            visTable(true) {
                defaults().pad(2f)
                visLabel("Hostname:") {
                    it.right()
                }
                add(hostnameTextField)
                row()
                visLabel("Username:") {
                    it.right()
                }
                add(usernameTextField)
                row()
                visLabel("Password:") {
                    it.right()
                }
                add(passwordTextField)
                row()
                visCheckBox("Remember me") {
                    it.colspan(2)
                }
                defaults().reset()
            }
            visTextButton("Connect") {
                pad(20f)
                onClick {
                    val (host, port) = hostnameTextField.text.split(":").let { it[0] to it[1].toInt() }

                    KtxAsync.launch {
                        recover({
                            ConnectionAttempted(
                                host,
                                port,
                                Credentials.UsernamePassword.Data(usernameTextField.text, passwordTextField.text)
                            ).publish()
                        }) {
                            it.handleUI(Connection)
                        }
                    }
                }
            }
        }
    }

