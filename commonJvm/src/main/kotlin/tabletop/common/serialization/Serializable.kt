package tabletop.common.serialization

import kotlinx.serialization.KSerializer

class Serializable<T>(val serializer: KSerializer<T>, val value: T) {
    companion object
}

inline fun <reified T> Serializable.Companion.with(
    serializer: KSerializer<T>,
    block: () -> T
) =
    Serializable(serializer, block())

context (Serialization)
fun <T> Serializable<T>.serialize(): String = json.encodeToString(serializer, value)