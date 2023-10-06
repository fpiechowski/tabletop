package tabletop.dnd5e

interface Modifier<T> {
    fun T.modify(): T
}
