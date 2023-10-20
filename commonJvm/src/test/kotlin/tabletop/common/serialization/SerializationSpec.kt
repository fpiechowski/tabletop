package tabletop.common.serialization

import arrow.core.raise.either
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.core.spec.style.FreeSpec
import tabletop.common.command.Command
import tabletop.common.command.SignInCommandResult
import tabletop.common.error.CommonError
import tabletop.common.user.User

class SerializationSpec : FreeSpec({
    with(Serialization { }) {
        val signIn = Command.SignIn("principal", "secret")
        val signInCommandResult = SignInCommandResult(signIn, User("user"))
        val commandError = Command.Error("message")
        "serialize" - {
            "${Command.SignIn::class.qualifiedName}" {
                either { (signIn as Command).serialize() }
                    .shouldBeRight()
                    .shouldEqualJson(
                        """
                        {
                          "type": "${Command.SignIn::class.qualifiedName}",
                          "principal": "principal",
                          "secret": "secret"
                        }
                    """.trimIndent()
                    )
            }

            "${Command.Result::class.qualifiedName}" {
                either { (signInCommandResult as Command.Result<Command, Command.Result.Data>).serialize() }
                    .shouldBeRight()
                    .shouldEqualJson(
                        """
                        {
                          "type": "${SignInCommandResult::class.qualifiedName}",
                          "command": {
                            "principal": "${signInCommandResult.command.principal}",
                            "secret": "${signInCommandResult.command.secret}"
                          },
                          "data": {
                            "name": "${signInCommandResult.data.name}",
                            "id": "${signInCommandResult.data.id}"
                          }
                        }
                    """.trimIndent()
                    )
            }

            "${Command.Error::class.qualifiedName}" {
                either { (commandError as CommonError).serialize() }
                    .shouldBeRight()
                    .shouldEqualJson(
                        """
                        {
                          "type": "tabletop.common.command.Command.Error",
                          "message": "message"
                        }
                    """.trimIndent()
                    )
            }
        }

        "deserialize" - {
            "${Command.SignIn::class.qualifiedName}" {
                either {
                    deserialize<Command>(
                        """
                          {
                            "type": "${Command.SignIn::class.qualifiedName}",
                            "principal": "${signIn.principal}",
                            "secret": "${signIn.secret}"
                          }
                    """.trimIndent()
                    )
                }.shouldBeRight(signIn)
            }

            "${Command.Result::class.qualifiedName}" {
                either {
                    deserialize<Command.Result<Command, Command.Result.Data>>(
                        """
                        {
                          "type": "${SignInCommandResult::class.qualifiedName}",
                          "command": {
                            "principal": "${signInCommandResult.command.principal}",
                            "secret": "${signInCommandResult.command.secret}"
                          },
                          "data": {
                            "name": "${signInCommandResult.data.name}",
                            "id": "${signInCommandResult.data.id}"
                          }
                        }
                    """.trimIndent()
                    )
                }.shouldBeRight(signInCommandResult)
            }

            "${Command.Error::class.qualifiedName}" {
                either {
                    deserialize<Command.Error>(
                        """
                        {
                          "type": "tabletop.common.command.Command.Error",
                          "message": "message"
                        }
                    """.trimIndent()
                    )
                }.shouldBeRight(commandError)
            }
        }
    }
})