package tabletop.common.dnd5e.character

import tabletop.common.Named
import java.io.Serializable


abstract class Race : Named, Serializable {

    abstract val age: Age
    abstract val alignment: Set<Character.Alignment>
    abstract val size: Character.Size
    abstract val speed: Int

    companion object {
        private const val serialVersionUID = 1L
    }

    data class Age(
        val adulteryInYears: Int,
        val lifespanInYears: Int
    ) : Serializable {
        companion object {
            private const val serialVersionUID = 1L
        }
    }
}

object Human : Race() {

    private fun readResolve(): Any = Human
    override val name: String = "Human"
    override val age: Age = Age(20, 100)
    override val alignment: Set<Character.Alignment> = setOf()
    override val size: Character.Size = Character.Size.Medium
    override val speed: Int = 30

    private const val serialVersionUID = 1L
}
