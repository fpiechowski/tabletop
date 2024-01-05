package tabletop.shared.dnd5e.character

import arrow.core.raise.either
import arrow.optics.PLens
import arrow.optics.copy
import tabletop.shared.error.CommonError
import tabletop.shared.error.IllegalStateError

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

fun Character.Companion.attribute(attribute: Character.Attribute) =
    either {
        PLens<Character, Character, Int, Int>(
            get = { it.attributes[attribute] ?: raise(IllegalStateError("Attribute $attribute not found in character $it")) },
            set = { character, attributeValue ->
                when (character) {
                    is PlayerCharacter -> character.copy {
                        PlayerCharacter.attributes.set(character.attributes.copy {
                            attribute.lens.set(attributeValue)
                        })
                    }

                    is NonPlayerCharacter -> character.copy {
                        NonPlayerCharacter.attributes.set(character.attributes.copy {
                           attribute.lens.set(attributeValue)
                        })
                    }

                    else -> character
                }
            }
        )
    }