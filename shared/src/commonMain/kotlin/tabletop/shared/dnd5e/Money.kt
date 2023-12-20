package tabletop.shared.dnd5e

import kotlinx.serialization.Serializable

@Serializable
data class Money(
    val cp: Int,
    val sp: Int,
    val ep: Int,
    val gp: Int,
    val pp: Int
) {
    companion object {
        val zero: Money = Money(0, 0, 0, 0, 0)
        operator fun invoke(
            cp: Int = 0,
            sp: Int = 0,
            ep: Int = 0,
            gp: Int = 0,
            pp: Int = 0
        ): Money = Money(cp, sp, ep, gp, pp)
    }
}
