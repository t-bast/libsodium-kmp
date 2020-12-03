import kotlin.test.Test
import kotlin.test.assertNotEquals

class SodiumTests {
    private val sodium = Sodium.init()

    @Test
    fun generateRandomInt() {
        assertNotEquals(0, sodium.random())
    }

}