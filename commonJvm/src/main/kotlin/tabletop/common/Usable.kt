package tabletop.common

interface Usable<USER : Usable.User, TARGET : Usable.Targetable> {
    fun use(game: Game, user: USER, targets: Set<TARGET>)

    interface User
    interface Targetable
}
