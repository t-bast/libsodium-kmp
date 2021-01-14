package org.cybele.sodium

import java.util.*

internal actual fun initSodium(): Sodium =
    tryLoad("android")
        ?: tryLoad("jvm")
        ?: error("Could not load native Sodium JNI library. Make sure you added the JNI dependency correctly.")

private fun tryLoad(platform: String): Sodium? {
    return try {
        val cls = Class.forName("org.cybele.sodium.jni.NativeSodium${platform.capitalize(Locale.ROOT)}Loader")
        val load = cls.getMethod("load")
        load.invoke(null) as Sodium
    } catch (ex: ClassNotFoundException) {
        null
    }
}