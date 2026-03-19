package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.parser.Parser
import kotlin.test.Test
import kotlin.test.assertEquals

class MathTest {
    @Test
    fun testDivide() {
        val expr = divide(Parser.parse("Pow[X, 2]"), X)
        assertEquals(expr, X)
    }

}
