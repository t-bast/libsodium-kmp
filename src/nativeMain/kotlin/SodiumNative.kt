import kotlinx.cinterop.*
import libsodium.*

// TODO: make that an object
internal class SodiumNative : Sodium {
    init {
        sodium_init()
    }

    override fun random(): Int {
        return randombytes_random().toInt()
    }
}

internal actual fun initSodium() : Sodium = SodiumNative()