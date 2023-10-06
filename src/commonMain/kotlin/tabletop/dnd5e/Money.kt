package tabletop.dnd5e

data class Money(
    val cp: Int,
    val sp: Int,
    val ep: Int,
    val gp: Int,
    val pp: Int
) {
    companion object {
        val zero: Money = Money(0, 0, 0, 0, 0)
    }
}
