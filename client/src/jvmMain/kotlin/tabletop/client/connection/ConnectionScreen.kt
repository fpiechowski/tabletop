package tabletop.client.connection

import arrow.core.raise.catch
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onChange
import ktx.app.clearScreen
import ktx.scene2d.actor
import ktx.scene2d.label
import ktx.scene2d.vis.visTextButton
import tabletop.client.auth.connectionWindow
import tabletop.client.event.Event
import tabletop.client.input.Input
import tabletop.client.input.InputAdapter
import tabletop.client.state.State
import tabletop.client.ui.StageScreen
import tabletop.client.ui.UserInterface
import tabletop.common.error.CommonError
import tabletop.common.error.handleTerminal
import tabletop.common.serialization.Serialization

context (Serialization, Input, UserInterface, Event.Processor, State)
class ConnectionScreen : StageScreen() {
    override val stage = Stage(FitViewport(1920f, 1080f))

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = InputMultiplexer(stage, InputAdapter())
    }

    init {
        catch({
            stage.addActor(table)
            table.run {
                label("Tabletop") {
                    it.top().left().expandX()
                    color = Color.BLACK
                }

                visTextButton("Close X") {
                    it.top().right()
                    color = Color.BLACK

                    onChange {
                        Gdx.app.exit()
                    }
                }

                row()

                actor(connectionWindow()) {
                    it.center().expand().colspan(2)
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
        stage.dispose()
    }
}