package tabletop.common.event

import tabletop.common.error.CommonError


interface Event {


    class Error(override val message: String?, override val cause: CommonError?) : CommonError()

    companion object
}

interface RequestEvent : Event

interface ResultEvent : Event {
    val shared: Boolean
}