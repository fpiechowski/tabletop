package tabletop.client.scene.token

import dev.fritz2.core.RenderContext
import tabletop.common.scene.token.Token

fun RenderContext.tokenView(token: Token<*>) =
    TokenView().run { render() }


class TokenView {

    fun RenderContext.render() =
        div {
            TODO()
        }
}



