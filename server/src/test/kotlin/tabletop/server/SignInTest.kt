package tabletop.server

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.uuid.UUID
import tabletop.common.auth.Authentication
import tabletop.common.auth.Credentials
import tabletop.common.command.Command
import tabletop.common.command.SignInCommandResult
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.serialization.Serialization
import tabletop.common.user.User
import tabletop.server.persistence.Persistence

class SignInTest : AnnotationSpec() {

    private val testUser = User("test", UUID("982b02d4-3253-476c-897a-3f1f98ff7541"))
    private val testUserCredentials = Credentials.UsernamePassword("username", "password")


    @Test
    fun signIn() {
        with(Serialization { }) {
            with(Authentication) {
                with(Persistence) {
                    persistenceRoot.users[testUser.id] = testUser
                    persistenceRoot.credentials[testUser] = testUserCredentials

                    with(ServerAdapter()) {
                        testApplication {
                            application {
                                application.complete(this)
                                module()
                            }

                            val client = createClient {
                                install(WebSockets)
                            }

                            val signIn = Command.SignIn(testUserCredentials.principal, testUserCredentials.secret)

                            client.webSocket {
                                with(Connection(this, TMVar.empty())) {
                                    send(Frame.Text(json.encodeToString<Command>(signIn)))

                                    val resultFrameText = (incoming.receive() as Frame.Text).readText()

                                    val result =
                                        json.decodeFromString<Command.Result<Command, Command.Result.Data>>(
                                            resultFrameText
                                        ).shouldBeEqual(SignInCommandResult(signIn, testUser))

                                    atomically {
                                        connections.first().authenticatedUser.read().shouldBeEqual(result.data)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun invalidCredentials() {
        with(Serialization { }) {
            with(Authentication) {
                with(Persistence) {
                    persistenceRoot.users[testUser.id] = testUser
                    persistenceRoot.credentials[testUser] = testUserCredentials

                    with(ServerAdapter()) {
                        testApplication {
                            application {
                                application.complete(this)
                                module()
                            }

                            val client = createClient {
                                install(WebSockets)
                            }

                            val signIn =
                                Command.SignIn(testUserCredentials.principal + "x", testUserCredentials.secret + "x")

                            client.webSocket {
                                with(Connection(this, TMVar.empty())) {
                                    send(Frame.Text(json.encodeToString<Command>(signIn)))

                                    val resultFrameText = (incoming.receive() as Frame.Text).readText()

                                    json.decodeFromString<CommonError>(
                                        resultFrameText
                                    ).shouldBeEqualToComparingFields(
                                        Command.Error(
                                            "Error on executing $signIn",
                                            Authentication.Error("Invalid credentials")
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}