package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.*
import kotlin.test.*

class ParserTest {

    @Test
    fun testParseSimpleSymbol() {
        val result = parse("x")
        assertEquals(result, X)
    }

    @Test
    fun testParseNumber() {
        val result = parse("42")
        assertEquals(result, integer(42))
    }

    @Test
    fun testParseNegativeNumber() {
        val result = parse("-314")
        assertEquals(result, integer(-314))
    }

    @Test
    fun testParseFunctionApplication() {
        val result = parse("Add[x]")
        assertTrue(result is Add)

        assertEquals(1, result.summands.size)
    }

    @Test
    fun testParseMultipleArguments() {
        val result = parse("Plus[1, 2, 3]")
        assertTrue(result is Add)
    }

    @Test
    fun testParseNestedExpressions() {
        val result = parse("Add[Sin[x], Cos[x]]")
        assertTrue(result is Add)

        assertEquals(2, result.summands.size)
        assertTrue(result.summands[0] is SinX)
        assertTrue(result.summands[1] is CosX)
    }

    @Test
    fun testWhitespaceHandling() {
        val result = parse("  Add [ x , x ]  ")
        assertTrue(result is Add)
        assertEquals(2, result.summands.size)
    }

    @Test
    fun testInvalidSyntaxThrowsException() {
        assertFails {
            parse("Add[x")
        }
    }

    @Test
    fun testMissingClosingBracket() {
        assertFailsWith<ParseException> {
            parse("Add[x, x")
        }
    }

    @Test
    fun testUnexpectedCharacter() {
        assertFailsWith<ParseException> {
            parse("Add[x] garbage")
        }
    }
}

class IsqrtTest {

    @Test
    fun testSquareRoots() {
        assertEquals(0, isqrt(0))
        assertEquals(1, isqrt(1))
        assertEquals(2, isqrt(4))
        assertEquals(3, isqrt(9))
        assertEquals(10, isqrt(100))
    }

    @Test
    fun testNonPerfectSquares() {
        assertEquals(null, isqrt(15))
        assertEquals(null, isqrt(17))
        assertEquals(null, isqrt(1000))
    }

    @Test
    fun testLargeNumbers() {
        assertEquals(1000, isqrt(1000000))
        assertEquals(null, isqrt(999999))
    }

    @Test
    fun testNegativeNumberThrows() {
        assertFailsWith<IllegalArgumentException> {
            isqrt(-1)
        }
    }

    @Test
    fun testIntVersion() {
        assertEquals(10, isqrt(100))
        assertEquals(null, isqrt(1000))
    }
}
