package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.parser.Parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MathTest {
    @Test
    fun testDivide() {
        val expr = divide(Parser.parse("Pow[X, 2]"), X)
        assertEquals(expr, X)
    }

    @Test
    fun testDifferentiate() {
        val m = multiply(divide(1, 3), xPow(3))
        val expr = differentiate(m)
        assertIs<XPow>(expr)
        assertEquals(expr.pow, integer(2))
    }

}
