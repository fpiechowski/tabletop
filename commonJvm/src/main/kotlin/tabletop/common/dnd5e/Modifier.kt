package tabletop.common.dnd5e

interface Modifier<T> {
    fun T.modify(): T
}
