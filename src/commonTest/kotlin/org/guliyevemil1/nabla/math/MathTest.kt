package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.parser.parse
import kotlin.test.Test
import kotlin.test.assertEquals

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
            expected = "(scale 3 (xpow 1))",
            actual = add(
                parse("(scale 2 x)"),
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
                        (scale -1 (* 
                            (xpow -2) 
                            (exp x)
                        ))
                        (pow (exp x) 2)
                    )
                    (/ 
                        (scale -1 (* 
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
            (+ 
                (/ 
                    (scale 2 (* (xpow 1) (exp x))) 
                    (pow (exp x) 2)
                )
                (/ 
                    (scale -1 (* (xpow 2) (exp x)))
                    (pow (exp x) 2)
                )
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
        assertEqualsExpr(
            "(+ (/ (scale 2 (exp x)) (pow (exp x) 2)) (/ (scale -2 (* x (exp x))) (pow (exp x) 2)))",
            differentiate(parse("(/ (scale 2 x) (exp x))"))
        )
    }

    @Test
    fun testDifferentiate6() {
        assertEqualsExpr(
            expected = """
                (+ 
                    (/ 
                        (* x (exp x) (sin x)) 
                        (* 
                            (pow (exp x) 2) 
                            (pow (cos x) 2)
                        )
                    )
                    (/ 
                        (scale -1 (* x (exp x) (cos x))) 
                        (* 
                            (pow (exp x) 2) 
                            (pow (cos x) 2)
                        )
                    )
                    (/ 
                        (* (exp x) (cos x))
                        (* 
                            (pow (exp x) 2) 
                            (pow (cos x) 2)
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
                (scale 
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
    fun testSqrt() {

        assertEqualsExpr(
            expected = """(scale 
                    (pow 24 (/ -1 2))
                    (xpow 2)
                )
                """.trimIndent(),
            actual = sqrt(
                parse(
                    """(scale (/ 1 24) (xpow 4))"""
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
    fun testNegate() {
        assertEqualsExpr(
            expected = "(scale -1 (* (xpow 1) (exp x)))",
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
