package org.cybele.sodium

public object NativeSodium : Sodium {
    init {
        require(SodiumJNI.sodium_init() == 0) { "could not initialize libsodium" }
    }

    override fun random(): Int {
        return SodiumJNI.randombytes_random()
    }
}