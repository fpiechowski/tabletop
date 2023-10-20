package tabletop.common.logging

import io.github.oshai.kotlinlogging.KotlinLogging


inline val <reified T> T.logger get() = KotlinLogging.logger(T::class.qualifiedName ?: "UnknownLogger")
