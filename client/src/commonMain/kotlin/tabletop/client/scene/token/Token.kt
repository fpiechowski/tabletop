package tabletop.client.scene.token

import korlibs.image.format.readBitmap
import korlibs.io.file.std.applicationVfs
import korlibs.korge.view.image
import korlibs.korge.view.xy
import korlibs.math.geom.Point
import tabletop.client.game.GameScene
import tabletop.common.scene.token.Token
import tabletop.common.geometry.Point as CommonPoint


suspend fun GameScene.tokenView(token: Token<*>) =
    tokenContainer.await().image(applicationVfs[token.imageFilePath].readBitmap()) {
        xy(token.position.toKorge())
    }

private fun CommonPoint.toKorge(): Point = Point(x, y)

