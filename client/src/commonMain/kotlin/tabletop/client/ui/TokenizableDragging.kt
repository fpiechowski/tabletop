package tabletop.client.ui

import androidx.compose.ui.geometry.Offset
import tabletop.shared.scene.token.Tokenizable

data class TokenizableDragging(
    val tokenizable: Tokenizable,
    val offset: Offset
)