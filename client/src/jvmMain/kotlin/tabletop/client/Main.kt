package tabletop.client

import arrow.core.raise.catch
import arrow.core.raise.recover
import arrow.fx.stm.TMVar
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.app.KtxGame
import ktx.assets.toInternalFile
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import ktx.scene2d.Scene2DSkin
import tabletop.client.connection.ConnectionScreen
import tabletop.client.error.handleUI
import tabletop.client.event.Event
import tabletop.client.event.process
import tabletop.client.game.GameScreen
import tabletop.client.input.Input
import tabletop.client.input.InputAdapter
import tabletop.client.lwjgl3.lwjgl3Application
import tabletop.client.serialization.buildSerializersModule
import tabletop.client.state.State
import tabletop.client.ui.StageScreen
import tabletop.client.ui.UserInterface
import tabletop.common.command.Command
import tabletop.common.error.CommonError
import tabletop.common.error.handleTerminal
import tabletop.common.serialization.Serialization
import tabletop.common.startProcessing

fun main() {
    with(runBlocking { State(TMVar.empty(), TMVar.empty(), TMVar.empty()) }) {
        with(Serialization { buildSerializersModule() }) {
            with(Input) {
                with(Command.Processor()) {
                    with(Command.Result.Processor()) {
                        with(Event.Processor()) {
                            with(UserInterface()) {
                                lwjgl3Application()
                            }
                        }
                    }
                }
            }
        }
    }
}

context (Serialization, Input, State, UserInterface, Event.Processor, Command.Processor)
class Main : KtxGame<StageScreen>() {
    lateinit var connectionScreen: ConnectionScreen
    lateinit var gameScreen: GameScreen
    lateinit var font: BitmapFont
    lateinit var skin: Skin

    val currentStageScreen get() = currentScreen as StageScreen

    override fun create() {
        catch(
            block = {
                with(Gdx.graphics) {
                    setFullscreenMode(displayMode)
                }

                VisUI.load()
                KtxAsync.initiate()

                KtxAsync.launch(newSingleThreadAsyncContext()) {
                    startProcessing<Event> {
                        recover({ it.process() }) {
                            it.handleUI(Event.Processor)
                        }
                    }
                }

                Scene2DSkin.defaultSkin = VisUI.getSkin()

                skin = VisUI.getSkin()
                font = BitmapFont("ui/segoe-ui.fnt".toInternalFile())

                Gdx.input.inputProcessor = InputAdapter()
                main.complete(this@Main)
                connectionScreen = ConnectionScreen()
                gameScreen = GameScreen()

                addScreen(connectionScreen)
                addScreen(gameScreen)
                setScreen<ConnectionScreen>()
            },
            catch = {
                CommonError.ThrowableError(it).handleTerminal(Main)
            }
        )
    }

    companion object
}


