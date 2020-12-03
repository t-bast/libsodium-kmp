internal class SodiumJni : Sodium {
    override fun random(): Int {
        TODO("Not yet implemented")
    }
}

internal actual fun initSodium(): Sodium = SodiumJni()