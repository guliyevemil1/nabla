package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.parser.parse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

fun assertEqualsExpr(expected: String, actual: Expr<*>) = assertEquals(parse(expected).toLisp(), actual.toLisp())

class MathTest {
    @Test
    fun testDivide() {
        assertEqualsExpr(
            expected = "x",
            actual = divide(parse("(xpow 2)"), X),
        )
    }

    @Test
    fun testDivide2() {
        assertEqualsExpr(
            expected = """
                (/ 
                    (+ 
                        (exp x) 
                        (scale -1 (* 
                            (xpow -2) 
                            (exp x)
                        ))
                        (scale -1 (* 
                            (xpow -1) 
                            (exp x)
                        ))
                    )
                    (pow (exp x) 2)
                )""".trimIndent(),
            actual = differentiate(parse("(/ (add -1 (xpow -1)) (exp x))")),
        )
    }

    //    fun testDivide3() {
    //        assertEqualsExpr(
    //            expected = "x",
    //            actual = divide(parse("(pow (xpow 1) 2)"), X),
    //        )
    //    }

    @Test
    fun testDifferentiate() {
        assertEqualsExpr(
            expected = "(xpow 2)",
            actual = differentiate(parse("(scale (/ 1 3) (xpow 3))"))
        )
    }

    @Test
    fun testDifferentiate2() {
        assertEqualsExpr(
            expected = """
            (+
                (scale 2 (* (xpow 1) (exp x) (sin x)))
                (* (xpow 2) (exp x) (sin x))
                (* (xpow 2) (exp x) (cos x))
            )
        """,
            actual = differentiate(parse("(* (xpow 2) (exp x) (sin x))")),
        )
    }

    @Test
    fun testDifferentiate3() {
        assertEqualsExpr(
            expected = """
            (/ 
                (+ 
                    (scale 2 (* (xpow 1) (exp x))) 
                    (scale -1 (* (xpow 2) (exp x)))
                )
                (pow (exp x) 2)
            )
        """,
            actual = differentiate(parse("(/ (xpow 2) (exp x))"))
        )
    }

    @Test
    fun testDifferentiate4() {
        assertEqualsExpr(
            expected = "(scale 2 (xpow 1))",
            actual = differentiate(X2),
        )
    }

    @Test
    fun testDifferentiate5() {
        val m = divide(scale(integer(2), X), ExpX)
        val expr = differentiate(m)
        assertIs<Divide<Any?>>(expr)
    }

    @Test
    fun testNegate() {
        assertEqualsExpr("(scale -1 (* (xpow 1) (exp x)))", negate(multiply(X, ExpX)))
    }

    @Test
    fun testAdd() {
        assertEqualsExpr(
            expected = "(scale 3 (xpow 1))",
            actual = add(
                parse("(scale 2 x)"),
                parse("x"),
            )
        )
    }

    @Test
    fun testAdd2() {
        assertEqualsExpr(
            expected = "(add (scale 3 (xpow 1)) (exp x))",
            actual = add(
                parse("(scale 2 x)"),
                parse("(exp x)"),
                parse("x"),
            )
        )
    }

    @Test
    fun testAdd3() {
        assertEqualsExpr(
            expected = "(+ (scale 2 (cos x)) (scale -4 (* x (sin x))) (scale -1 (* (xpow 2) (cos x))))",
            actual = add(
                parse("(scale 2 (cos x)) "),
                parse("(scale -2 (* x (sin x))) "),
                parse("(scale -2 (* x (sin x))) "),
                parse("(scale -1 (* (xpow 2) (cos x)))"),
            ),
        )
    }

    @Test
    fun testEquals() {
        assertEquals(
            add(X2, X, One).toLisp(),
            add(One, X, X2).toLisp(),
        )
    }

    @Test
    fun testLatex() {
        val m = Pow(X2, integer(2))
        assertEquals("""\left(x^{2}\right)^2""", m.render())
    }

}
