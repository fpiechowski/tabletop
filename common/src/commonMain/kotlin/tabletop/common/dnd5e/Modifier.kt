package tabletop.common.dnd5e

fun interface Modifier<T> {
    fun T.modify(): T
}
