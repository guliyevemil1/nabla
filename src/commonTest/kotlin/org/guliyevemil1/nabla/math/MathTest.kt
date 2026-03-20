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

    @Test
    fun testDifferentiate2() {
        val m = multiply(
            listOf(
                X2,
                ExpX,
                SinX,
            )
        )
        val expr = differentiate(m)
        assertIs<Add<Any?>>(expr)
        assertEquals(expr.summands.size, 3)
        val expr0 = expr.summands[0]
        val expr1 = expr.summands[1]
        val expr2 = expr.summands[2]
        assertIs<Scale>(expr0)
        assertEquals(expr0.factor, integer(2))
        assertIs<Multiply<Any?>>(expr1)
        assertIs<Multiply<Any?>>(expr2)
    }

    @Test
    fun testDifferentiate3() {
        val m = divide(
            X2,
            ExpX,
        )
        val expr = differentiate(m)
        assertIs<Divide<Any?>>(expr)
    }

}
