package org.cybele.sodium

import kotlinx.cinterop.*
import libsodium.*

// TODO: make that an object
internal class SodiumNative : Sodium {
    init {
        require(sodium_init() == 0)  { "could not initialize libsodium" }
    }

    override fun random(): Int {
        return randombytes_random().toInt()
    }
}

internal actual fun initSodium() : Sodium = SodiumNative()