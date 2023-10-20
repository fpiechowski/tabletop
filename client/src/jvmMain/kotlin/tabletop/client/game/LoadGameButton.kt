package tabletop.client.game

import kotlinx.coroutines.launch
import ktx.actors.onChange
import ktx.async.KtxAsync
import ktx.scene2d.KWidget
import ktx.scene2d.actor
import ktx.scene2d.scene2d
import ktx.scene2d.vis.KVisTextButton
import ktx.scene2d.vis.visTextButton
import tabletop.client.ui.UserInterface
import tabletop.common.Game
import tabletop.common.connection.Connection
import tabletop.common.serialization.Serialization

context (UserInterface, Connection, Serialization)
fun <S> KWidget<S>.loadGameButton(
    gameListingItem: Game.Listing.Item,
    init: KVisTextButton.(S) -> Unit = {},
) =
    scene2d.visTextButton("Load") {
        onChange {
            KtxAsync.launch {

            }
        }
    }.let { actor(it, init) }