package tabletop.client.ui

import androidx.compose.foundation.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import arrow.core.Either
import arrow.core.raise.catch
import arrow.core.raise.either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tabletop.common.error.CommonError

@Composable
fun <T> AsyncImage(
    load: suspend () -> T?,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val imageResult: Either<CommonError, T?> by produceState<Either<CommonError, T?>>(Either.Right(null)) {
        value = withContext(Dispatchers.IO) {
            either {
                catch({
                    load()
                }) {
                    raise(CommonError.ThrowableError(it))
                }
            }
        }
    }

    imageResult.fold(
        ifLeft = { error -> Text(error.toString()) },
        ifRight = { image ->
            if (image != null) {
                Image(
                    painter = painterFor(image),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = modifier
                )
            } else {
                CircularProgressIndicator()
            }
        }
    )
}