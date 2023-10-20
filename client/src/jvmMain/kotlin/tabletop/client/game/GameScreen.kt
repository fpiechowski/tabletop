package tabletop.client.game

import arrow.core.raise.catch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport
import ktx.app.clearScreen
import ktx.scene2d.actors
import ktx.scene2d.vis.visTable
import tabletop.client.event.Event
import tabletop.client.input.Input
import tabletop.client.input.InputAdapter
import tabletop.client.ui.StageScreen
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError
import tabletop.common.error.handleTerminal
import tabletop.common.serialization.Serialization

context (Serialization, Input, UserInterface, Event.Processor, tabletop.client.state.State)
class GameScreen : StageScreen() {
    override val stage: Stage = Stage(ScalingViewport(Scaling.fit, 1920f, 1080f))

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = InputMultiplexer(stage, InputAdapter())
    }

    init {
        catch({
            stage.actors {
                visTable {
                    setFillParent(true)
                }
            }
        }) {
            CommonError.ThrowableError(it).handleTerminal(this)
        }
    }

    override fun render(delta: Float) {
        clearScreen(red = 0.7f, green = 0.7f, blue = 0.7f)

        stage.act(Gdx.graphics.deltaTime.coerceAtMost(1 / 30f))
        stage.draw()
    }

    override fun dispose() {
    }
}