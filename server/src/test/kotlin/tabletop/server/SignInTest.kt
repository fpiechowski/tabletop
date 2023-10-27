package tabletop.server

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.kotest.matchers.equals.shouldBeEqual
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.uuid.UUID
import one.microstream.storage.embedded.types.EmbeddedStorage
import tabletop.common.auth.Authentication
import tabletop.common.auth.Credentials
import tabletop.common.command.Command
import tabletop.common.command.SignInCommandResult
import tabletop.common.connection.Connection
import tabletop.common.error.CommonError
import tabletop.common.user.User
import tabletop.server.di.DependenciesAdapter
import tabletop.server.persistence.Persistence

class SignInTest : StringSpec() {

    private val testUser = User("test", UUID("982b02d4-3253-476c-897a-3f1f98ff7541"))

    private val testUserCredentials = Credentials.UsernamePassword("username", "password")

    private val embeddedStorageManager = EmbeddedStorage.start(Persistence.Root)

    init {

        "signIn" {
            val dependenciesAdapter = DependenciesAdapter(lazy { Persistence(embeddedStorageManager) })

            with(dependenciesAdapter.persistence) {
                persistenceRoot.users[testUser.id] = testUser
                persistenceRoot.credentials[testUser] = testUserCredentials
            }

            with(dependenciesAdapter.serialization) {
                with(dependenciesAdapter.serverAdapter) {
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

        "invalidCredentials" {
            val dependenciesAdapter = DependenciesAdapter(lazy { Persistence(embeddedStorageManager) })

            with(dependenciesAdapter.persistence) {
                persistenceRoot.users[testUser.id] = testUser
                persistenceRoot.credentials[testUser] = testUserCredentials
            }
            with(dependenciesAdapter.serialization) {
                with(dependenciesAdapter.serverAdapter) {
                    testApplication {
                        application {
                            application.complete(this)
                            module()
                        }

                        val client = createClient {
                            install(WebSockets)
                        }

                        val signIn = Command.SignIn(
                            testUserCredentials.principal + "x",
                            testUserCredentials.secret + "x"
                        )

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
