package tabletop.common.dice

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
class Dice(val dice: Int, val sides: Int) {

    fun roll(): Int =
        (1..dice)
            .map { Random.nextInt(sides) }
            .sum()


    override fun toString() = "${dice}d$sides"
}

infix fun Int.d(sides: Int) = Dice(this, sides)
