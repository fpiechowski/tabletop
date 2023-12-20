package tabletop.shared.dnd5e.character

import kotlinx.serialization.Serializable
import tabletop.shared.entity.Named


@Serializable
abstract class Race : Named {

    abstract val age: Age
    abstract val alignment: Set<Character.Alignment>
    abstract val size: Character.Size
    abstract val speed: Int


    @Serializable
    data class Age(
        val adulteryInYears: Int,
        val lifespanInYears: Int
    )
}

@Serializable
class Human : Race() {

    private fun readResolve(): Any = Human
    override val name: String = "Human"
    override val age: Age = Age(20, 100)
    override val alignment: Set<Character.Alignment> = setOf()
    override val size: Character.Size = Character.Size.Medium
    override val speed: Int = 30
}
