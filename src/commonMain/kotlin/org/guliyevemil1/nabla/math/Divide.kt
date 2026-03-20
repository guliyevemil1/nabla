package org.guliyevemil1.nabla.math

data class Divide<T>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T> {
    override fun render(): String =
        """\frac{${numerator.render()}}{${denominator.render()}}"""

    override fun toLisp(): String = "(/ ${numerator.toLisp()} ${denominator.toLisp()})"
}

fun divide(l: Integral, r: Integral): Expr<Nothing> {
    if (l is Integer && r is Integer) return rational(l.n, r.n)
    val ratL = l.toRational() ?: return Illegal
    val ratR = l.toRational() ?: return Illegal
    return rational(
        numerator = ratL.numerator * ratR.denominator,
        denominator = ratL.denominator * ratR.numerator,
    )
}

fun <T> divide(l: Expr<T>, r: Expr<T>): Expr<T> =
    when {
        r == Zero -> Illegal
        l == Zero -> Zero
        r == One -> l
        l is Illegal || r is Illegal -> Illegal
        l is Integral && r is Integral -> {
            divide(l, r)
        }

        l is Divide -> divide(
            l.numerator,
            multiply(l.denominator, r),
        )

        l is Scale && r is Scale -> Scale(
            factor = divide(l.factor, r.factor),
            expr = divide(l.expr, r.expr),
        ) as Expr<T>

        l is XPow && r is XPow -> xPow(add(l.pow, multiply(NegOne, r.pow))) as Expr<T>

        l == r -> One
        else -> Divide(l, r)
    }
