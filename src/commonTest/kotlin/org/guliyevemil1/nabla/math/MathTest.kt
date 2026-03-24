package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.parser.parse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

fun assertEqualsExpr(expected: String, actual: Expr<*>) = assertEquals(parse(expected).toLisp(), actual.toLisp())

class MathTest {
    @Test
    fun testEquals() {
        assertEqualsExpr(
            "(+ 1 x (xpow 2))",
            add(parse("(+ (xpow 2) x 1)")),
        )
    }

    @Test
    fun testAdd() {
        assertEqualsExpr(
            expected = "(* 3 (xpow 1))",
            actual = add(
                parse("(* 2 x)"),
                parse("x"),
            )
        )
    }

    @Test
    fun testAddInts() {
        assertEqualsExpr(
            expected = "3",
            actual = add(
                parse("1"),
                parse("2"),
            )
        )
    }

    @Test
    fun testAdd2() {
        assertEqualsExpr(
            expected = "(add (* 3 (xpow 1)) (exp x))",
            actual = add(
                parse("(* 2 x)"),
                parse("(exp x)"),
                parse("x"),
            )
        )
    }

    @Test
    fun testAdd3() {
        assertEqualsExpr(
            expected = "(+ (* 2 (cos x)) (* -4 (* x (sin x))) (* -1 (* (xpow 2) (cos x))))",
            actual = add(
                parse("(* 2 (cos x)) "),
                parse("(* -2 (* x (sin x))) "),
                parse("(* -2 (* x (sin x))) "),
                parse("(* -1 (* (xpow 2) (cos x)))"),
            ),
        )
    }

    @Test
    fun testLog() {
        assertEqualsExpr(
            "(xpow (/ 1 2))",
            sqrt(parse("x")),
        )
    }

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
                (+ 
                    (/ 
                        (exp x) 
                        (pow (exp x) 2)
                    )
                    (/ 
                        (* -1 (* 
                            (xpow -2) 
                            (exp x)
                        ))
                        (pow (exp x) 2)
                    )
                    (/ 
                        (* -1 (* 
                            (xpow -1) 
                            (exp x)
                        ))
                        (pow (exp x) 2)
                    )
                )
            """.trimIndent(),
            actual = differentiate(parse("(/ (add -1 (xpow -1)) (exp x))")),
        )
    }

    @Test
    fun testDivide3() {
        assertEqualsExpr(
            expected = "x",
            actual = divide(parse("(pow (xpow 1) 2)"), X),
        )
    }

    @Test
    fun testDivide4() {
        assertEqualsExpr(
            expected = "(* -1 (xpow -1))",
            actual = divide(NegOne, X),
        )
    }

    @Test
    fun testDivide5() {
        assertEqualsExpr(
            expected = "(* x (exp (* -1 x)))",
            actual = divide(
                parse("(* x (exp x))"),
                parse("(pow (exp x) 2)"),
            ),
        )
    }

    @Test
    fun testDivide6() {
        assertEqualsExpr(
            expected = "(pow (cos x) -1)",
            actual = divide(
                parse("(cos x)"),
                parse("(pow (cos x) 2)"),
            ),
        )
    }

    @Test
    fun testDivide7() {
        assertEqualsExpr(
            expected = "(cos x)",
            actual = divide(
                parse("(* x (cos x))"),
                parse("x"),
            ),
        )
    }

    @Test
    fun testDifferentiate() {
        assertEqualsExpr(
            expected = "(xpow 2)",
            actual = differentiate(parse("(* (/ 1 3) (xpow 3))"))
        )
    }

    @Test
    fun testDifferentiate2() {
        assertEqualsExpr(
            expected = """
            (+
                (* 2 (* (xpow 1) (exp x) (sin x)))
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
            (+ 
                (* 2 (* (xpow 1) (exp (* -1 x)))) 
                (* -1 (* (xpow 2) (exp (* -1 x))))
            )
        """,
            actual = differentiate(parse("(/ (xpow 2) (exp x))"))
        )
    }

    @Test
    fun testDifferentiate4() {
        assertEqualsExpr(
            expected = "(* 2 (xpow 1))",
            actual = differentiate(X2),
        )
    }

    @Test
    fun testDifferentiate5() {
        assertEqualsExpr(
            """
                (+ 
                    (* 2 (exp (* -1 x))) 
                    (* -2 
                        (*
                            x 
                            (exp (* -1 x))
                        )
                    )
                )""".trimIndent(),
            differentiate(parse("(/ (* 2 x) (exp x))"))
        )
    }

    @Test
    fun testDifferentiate6() {
        assertEqualsExpr(
            expected = """
                (+ 
                    (/ 
                        (* x (sin x)) 
                        (* 
                            (exp x) 
                            (pow (cos x) 2)
                        )
                    )
                    (* -1
                        (/ 
                            x
                            (* 
                                (exp x) 
                                (cos x)
                            )
                        )
                    )
                    (/ 
                        1
                        (* 
                            (exp x) 
                            (cos x)
                        )
                    )
                ) 

                """.trimIndent(),
            actual = differentiate(
                parse(
                    """(/ x
                                  (*
                                    (exp x)
                                    (cos x)))"""
                )
            )
        )
    }

    @Test
    fun testDifferentiate7() {
        assertEqualsExpr(
            expected = """
                (* 
                    (/ -1 2) 
                    (* 
                        (sin x)
                        (pow (cos x) (/ -1 2))
                    ) 
                )""".trimIndent(),
            differentiate(parse("(pow (cos x) (/ 1 2))"))
        )
    }

    @Test
    fun testDifferentiate8() {
        assertEqualsExpr(
            "(+ (pow (cos x) -1) (* (* x (sin x)) (pow (cos x) -2)))",
            differentiate(parse("(/ x (cos x))"))
        )
    }

    @Test
    fun testSqrt() {

        assertEqualsExpr(
            expected = """(* 
                    (pow 24 (/ -1 2))
                    (xpow 2)
                )
                """.trimIndent(),
            actual = sqrt(
                parse(
                    """(* (/ 1 24) (xpow 4))"""
                )
            )
        )
    }

    @Test
    fun testSqrt2() {
        assertEqualsExpr(
            expected = """
                (xpow (/ 1 4))
                """.trimIndent(),
            sqrt(sqrt(X))
        )
    }

    @Test
    fun testIntegrate() {
        assertEqualsExpr(
            expected = """
                (* (/ 1 4) (xpow 4))
                """.trimIndent(),
            integrate(xPow(integer(3)))
        )
    }

    @Test
    fun testEqualsUpToConstant() {
        assertFalse(
            equalsUpToConstant(
                xPow(integer(3)),
                xPow(integer(1)),
            )
        )
    }

    @Test
    fun testNegate() {
        assertEqualsExpr(
            expected = "(* -1 (* (xpow 1) (exp x)))",
            actual = negate(multiply(X, ExpX))
        )
    }

    @Test
    fun testMultiply() {
        assertEqualsExpr(
            "(* (xpow 2) (sin x))",
            multiply(
                parse(
                    """(* x (sin x))"""
                ),
                X,
            ),
        )
        assertEqualsExpr(
            "(* (xpow 2) (sin x))",
            multiply(
                X,
                parse(
                    """(* x (sin x))"""
                ),
            ),
        )
        assertEqualsExpr(
            "(* (xpow 7) (sin x))",
            multiply(
                X,
                parse(
                    """(* x x x x x x (sin x))"""
                ),
            ),
        )
    }

    @Test
    fun testLatex() {
        val m = Pow(X2, integer(2))
        assertEquals("""\left(x^{2}\right)^{2}""", m.render())
    }

}
