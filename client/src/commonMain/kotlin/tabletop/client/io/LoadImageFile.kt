package tabletop.client.io

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import java.io.File

fun loadImageFile(file: File): ImageBitmap =
    file.inputStream().buffered().use(::loadImageBitmap)