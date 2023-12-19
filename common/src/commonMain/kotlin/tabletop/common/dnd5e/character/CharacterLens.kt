package tabletop.common.dnd5e.character

import arrow.optics.PLens
import arrow.optics.copy

val Character.Companion.name
    get() = PLens<Character, Character, String, String>(
        get = { it.name },
        set = { character, name ->
            when (character) {
                is PlayerCharacter -> character.copy {
                    PlayerCharacter.name.set(name)
                }

                is NonPlayerCharacter -> character.copy {
                    NonPlayerCharacter.name.set(name)
                }

                else -> character
            }
        }
    )

val Character.Companion.hp
    get() = PLens<Character, Character, Int, Int>(
        get = { it.hp },
        set = { character, hp ->
            when (character) {
                is PlayerCharacter -> character.copy {
                    PlayerCharacter.hp.set(hp)
                }

                is NonPlayerCharacter -> character.copy {
                    NonPlayerCharacter.hp.set(hp)
                }

                else -> character
            }
        }
    )

val Character.Companion.currentHp
    get() = PLens<Character, Character, Int, Int>(
        get = { it.hp },
        set = { character, currentHp ->
            when (character) {
                is PlayerCharacter -> character.copy {
                    PlayerCharacter.currentHp.set(currentHp)
                }

                is NonPlayerCharacter -> character.copy {
                    NonPlayerCharacter.currentHp.set(currentHp)
                }

                else -> character
            }
        }
    )

val Character.Companion.experience
    get() = PLens<Character, Character, Long, Long>(
        get = { it.experience },
        set = { character, experience ->
            when (character) {
                is PlayerCharacter -> character.copy {
                    PlayerCharacter.experience.set(experience)
                }

                is NonPlayerCharacter -> character.copy {
                    NonPlayerCharacter.experience.set(experience)
                }

                else -> character
            }
        }
    )