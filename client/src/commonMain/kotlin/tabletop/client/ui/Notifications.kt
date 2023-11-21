package tabletop.client.ui

import dev.fritz2.core.RenderContext
import dev.fritz2.core.Store
import dev.fritz2.core.storeOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tabletop.client.di.Dependencies
import tabletop.client.update

class Notifications(val dependencies: Dependencies) {
    val notifications: Store<List<Notification>> = storeOf(listOf(), Job())

    fun RenderContext.render() =
        div("absolute top-3 right-3 flex flex-col w-80 gap-3") {
            with(dependencies.uiErrorHandler) {
                launch {
                    notifications.data.render { notifications ->
                        notifications.forEach { notification ->
                            when (notification.type) {
                                Notification.Type.Error -> ::importantFrame
                                Notification.Type.Info -> ::frame
                            }.invoke {
                                div("flex w-full") {
                                    div("flex-grow") {
                                        +notification.text
                                    }
                                    div("justify-self-end material-symbols-outlined select-none") {
                                        +"close"

                                        clicks.handledBy {
                                            this@Notifications.notifications.update { it - notification }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
}

class Notification(val text: String, val type: Type) {
    enum class Type {
        Error, Info
    }
}