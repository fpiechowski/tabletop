package tabletop.client.scene.token

import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.annotations.KorgeExperimental
import korlibs.korge.internal.KorgeInternal
import korlibs.korge.view.image
import korlibs.korge.view.xy
import tabletop.client.game.GameScene
import tabletop.client.geometry.toKorge
import tabletop.common.scene.token.Token


@KorgeInternal
@KorgeExperimental
suspend fun GameScene.tokenView(token: Token<*>) =
    tokenContainer.await().image(applicationVfs[token.imageFilePath].readBitmap()) {
        xy(token.position.toKorge())
    }

