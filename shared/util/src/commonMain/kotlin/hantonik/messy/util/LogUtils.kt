package hantonik.messy.util

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KMarkerFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Marker
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

object LogUtils {
    @JvmStatic
    val fatal: Marker = KMarkerFactory.getMarker("FATAL")

    @JvmStatic
    fun getLogger(caller: KClass<*>): KLogger {
        return KotlinLogging.logger(caller.qualifiedName ?: "Anonymous class")
    }
}