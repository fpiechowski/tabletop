package tabletop.shared.dnd5e

import kotlinx.serialization.Serializable

@Serializable
abstract class Modifier<T>(
) {
    abstract val modify: T.() -> T
    abstract val value: T

    fun T.modify(): T = modify(this)
}

@Serializable
data class IntModifier(override val modify: Int.() -> Int) : Modifier<Int>() {
    override val value: Int
        get() = 0.modify()

    override fun toString(): String =
        if (value > 0) {
            "+$value"
        } else if (value < 0) {
            "$value"
        } else "$value"

}