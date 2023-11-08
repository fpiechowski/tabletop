package tabletop.client.scene.token

import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.render.RenderContext
import korlibs.korge.view.View
import korlibs.korge.view.image
import korlibs.korge.view.xy
import tabletop.client.game.GameScene
import tabletop.client.geometry.toKorge
import tabletop.common.scene.token.Token

class TokenView private constructor(private val delegate: View) : View() {
    companion object {
        @KorgeInternal
        @KorgeExperimental
        suspend fun GameScene.tokenView(token: Token<*>) =
            tokenContainer.await().image(applicationVfs[token.imageFilePath].readBitmap()) {
                xy(token.position.toKorge())
            }
    }

    override fun renderInternal(ctx: RenderContext) {
        delegate.render(ctx)
    }

}

@KorgeInternal
@KorgeExperimental
suspend fun GameScene.tokenView(token: Token<*>) =
    tokenContainer.await().image(applicationVfs[token.imageFilePath].readBitmap()) {
        xy(token.position.toKorge())
    }

