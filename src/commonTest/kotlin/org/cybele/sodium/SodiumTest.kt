package org.cybele.sodium

import kotlin.test.Test
import kotlin.test.assertNotEquals

class SodiumTests {
    private val sodium = Sodium.init()

    @Test
    fun generateRandomInt() {
        val r1 = sodium.random()
        val r2 = sodium.random()
        assertNotEquals(r1, r2)
    }

}