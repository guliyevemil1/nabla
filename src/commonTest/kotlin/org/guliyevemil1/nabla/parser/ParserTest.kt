package org.guliyevemil1.nabla.parser

import org.guliyevemil1.nabla.math.isqrt
import kotlin.test.*

class ParserTest {

    @Test
    fun testParseSimpleSymbol() {
        val result = parse("x")
        assertTrue(result is Expr.Symbol)
        assertEquals("x", result.name)
    }

    @Test
    fun testParseNumber() {
        val result = parse("42")
        assertTrue(result is Expr.Number)
        assertEquals(42.0, result.value)
    }

    @Test
    fun testParseNegativeNumber() {
        val result = parse("-3.14")
        assertTrue(result is Expr.Number)
        assertEquals(-3.14, result.value)
    }

    @Test
    fun testParseFunctionApplication() {
        val result = parse("f[x]")
        assertTrue(result is Expr.Application)

        assertTrue(result.function is Expr.Symbol)
        assertEquals("f", result.function.name)
        assertEquals(1, result.args.size)
    }

    @Test
    fun testParseMultipleArguments() {
        val result = parse("Plus[1, 2, 3]")
        assertTrue(result is Expr.Application)

        assertEquals("Plus", (result.function as Expr.Symbol).name)
        assertEquals(3, result.args.size)
    }

    @Test
    fun testParseNestedExpressions() {
        val result = parse("f[g[x], h[y]]")
        assertTrue(result is Expr.Application)

        assertEquals(2, result.args.size)
        assertTrue(result.args[0] is Expr.Application)
        assertTrue(result.args[1] is Expr.Application)
    }

    @Test
    fun testParseCurriedApplication() {
        val result = parse("f[x][y]")
        assertTrue(result is Expr.Application)

        assertTrue(result.function is Expr.Application)
        val inner = result.function
        assertEquals("f", (inner.function as Expr.Symbol).name)
    }

    @Test
    fun testParseEmptyArguments() {
        val result = parse("f[]")
        assertTrue(result is Expr.Application)
        assertEquals(0, result.args.size)
    }

    @Test
    fun testWhitespaceHandling() {
        val result = parse("  f [ x , y ]  ")
        assertTrue(result is Expr.Application)
        assertEquals(2, result.args.size)
    }

    @Test
    fun testInvalidSyntaxThrowsException() {
        assertFails {
            parse("f[x")
        }
    }

    @Test
    fun testMissingClosingBracket() {
        assertFailsWith<ParseException> {
            parse("f[x, y")
        }
    }

    @Test
    fun testUnexpectedCharacter() {
        assertFailsWith<ParseException> {
            parse("f[x] garbage")
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
