package tabletop.client.state

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.flow.MutableStateFlow
import tabletop.common.scene.Scene
import tabletop.common.scene.token.Token

class SceneState {
    val current: MutableStateFlow<Scene?> = MutableStateFlow(null)
    val selectedToken: MutableStateFlow<Token<*>?> = MutableStateFlow(null)
    val foregroundImage: ForegroundImage = ForegroundImage()
    class ForegroundImage {
        val scale: MutableStateFlow<Float> = MutableStateFlow(1f)
        val offset: MutableStateFlow<Offset?> = MutableStateFlow(null)
    }
}