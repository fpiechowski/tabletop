package tabletop.client.ui

import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.app.KtxScreen
import ktx.scene2d.scene2d
import ktx.scene2d.vis.KVisTable
import ktx.scene2d.vis.visTable

abstract class StageScreen : KtxScreen {
    abstract val stage: Stage
    val table: KVisTable = scene2d.visTable {
        debugAll()
        setFillParent(true)
    }

}