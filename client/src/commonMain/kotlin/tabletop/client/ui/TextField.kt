package tabletop.client.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import arrow.core.raise.recover
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import tabletop.shared.error.CommonError

@Composable
fun <T : Any> TextField(
    field: Field<T>,
    value: String,
    editable: Value<Boolean> = MutableValue(true),
    errors: MutableValue<Map<Field<*>, CommonError>> = MutableValue(mapOf()),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    singleLine: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    readOnly: Boolean? = null,
    onValueChange: (T) -> Unit,
) = recover({
    androidx.compose.material3.TextField(
        readOnly = readOnly ?: !editable.value,
        value = value,
        label = { Text(field.label) },
        isError = errors.value.containsKey(field),
        onValueChange = {
            onValueChange(field.fromString(it).bind())
        },
        textStyle = textStyle,
        modifier = modifier,
        singleLine = singleLine,
        colors = colors
    )
}) {
    Text(it.message ?: "Unknown Error")
}

@Composable
fun <T : Any> OutlinedTextField(
    field: Field<T>,
    value: String,
    editable: Value<Boolean> = MutableValue(true),
    errors: MutableValue<Map<Field<*>, CommonError>> = MutableValue(mapOf()),
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    singleLine: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    readOnly: Boolean? = null,
    errorHandler: (CommonError) -> Unit,
    onValueChange: (T) -> Unit,
) =
    androidx.compose.material3.OutlinedTextField(
        readOnly = readOnly ?: !editable.value,
        value = value,
        label = { Text(field.label) },
        isError = errors.value.containsKey(field),
        onValueChange = {
            recover({
                onValueChange(field.fromString(it).bind())
            }) {
                errorHandler(it)
            }
        },
        textStyle = textStyle,
        modifier = modifier,
        singleLine = singleLine,
        colors = colors
    )
