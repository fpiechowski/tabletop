package tabletop.client.io

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

actual fun loadImageFile(file: File): ImageBitmap =
    BitmapFactory.decodeFile(file.absolutePath).asImageBitmap()
