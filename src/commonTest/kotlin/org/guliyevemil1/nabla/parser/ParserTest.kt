package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.*
import kotlin.test.*

class ParserTest {

    @Test
    fun testParseSimpleSymbol() {
        assertEquals(X, parse("x"))
    }

    @Test
    fun testParseNumber() {
        assertEquals(integer(1), parse("1"))
        assertEquals(integer(2), parse("2"))
        assertEquals(integer(42), parse("42"))
    }

    @Test
    fun testParseNegativeNumber() {
        assertEquals(integer(-314), parse("-314"))
    }

    @Test
    fun testParseFunctionApplication() {
        val result = parse("(add x)")
        assertTrue(result is Add)
        assertEquals(1, result.summands.size)
    }

    @Test
    fun testParseMultipleArguments() {
        val result = parse("(add 1 2 3)")
        assertTrue(result is Add)
    }

    @Test
    fun testParseNestedExpressions() {
        val result = parse("(add (sin x) (cos x))")
        assertTrue(result is Add)

        assertEquals(2, result.summands.size)
        assertTrue(result.summands[0] is SinX)
        assertTrue(result.summands[1] is CosX)
    }

    @Test
    fun testWhitespaceHandling() {
        val result = parse("  (  Add  x  x )  ")
        assertTrue(result is Add)
        assertEquals(2, result.summands.size)
    }

    @Test
    fun testInvalidSyntaxThrowsException() {
        assertFails {
            parse("(add x")
        }
    }

    @Test
    fun testMissingClosingBracket() {
        assertFailsWith<ParseException> {
            parse("(add x x")
        }
    }

    @Test
    fun testUnexpectedCharacter() {
        assertFailsWith<ParseException> {
            parse("(add x) garbage")
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
