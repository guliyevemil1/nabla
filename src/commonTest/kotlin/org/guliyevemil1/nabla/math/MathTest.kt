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
        val m = multiply(divide(integer(1), integer(3)), xPow(3))
        val expr = differentiate(m)
        assertEquals("(xpow 2)", expr.toLisp())
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

    @Test
    fun testDifferentiate4() {
        val m = X2
        val expr = differentiate(m)
        assertIs<Scale>(expr)
        assertEquals(integer(2), expr.factor)
        assertEquals(X, expr.expr)
    }

    @Test
    fun testDifferentiate5() {
        val m = divide(scale(integer(2), X), ExpX)
        val expr = differentiate(m)
        assertIs<Divide<Any?>>(expr)
    }

    @Test
    fun testNegate() {
        val m = multiply(
            X,
            ExpX,
        )
        val expr = negate(m)
        assertIs<Scale>(expr)
        assertEquals(NegOne, expr.factor)
        assertEquals(m, expr.expr)
    }

    @Test
    fun testLatex() {
        val m = Pow(X2, 2)
        assertEquals("""\left(x^{2}\right)^2""", m.render())
    }

    @Test
    fun testAdd() {
        val m = add(listOf(scale(integer(2), X), X))
        assertEquals("(scale 3 (xpow 1))", m.toLisp())
    }

}
