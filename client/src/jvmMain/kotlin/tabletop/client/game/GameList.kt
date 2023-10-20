package tabletop.client.game

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.utils.Align
import ktx.assets.toInternalFile
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visLabel
import ktx.scene2d.vis.visTable
import tabletop.client.state.State
import tabletop.client.ui.UserInterface
import tabletop.common.Game
import tabletop.common.connection.Connection
import tabletop.common.serialization.Serialization

object GameList


context (UserInterface, Connection, Serialization, State)
fun gameList(gameListing: Game.Listing) = scene2d.visTable {
    setFillParent(true)
    //gameListingsChannel.receive()

    if (gameListing.games.isEmpty()) {
        visLabel("No games found") {
            it.expand()
        }
    }
    gameListing.games.forEach { item ->
        visTable {
            visLabel(item.name) {
                it.expandX()
                it.fillX()
                this.setAlignment(Align.center)
            }
            row()
            visImage(Texture("libgdx128.png".toInternalFile())) {
                it.fill()
            }
            row()
            loadGameButton(item) {
                it.fill()
                it.expand()
            }
        }
    }
}