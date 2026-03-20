package org.guliyevemil1.nabla.math

data class Divide<T>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T> {
    override fun render(): String =
        """\frac{${numerator.render()}}{${denominator.render()}}"""

    override fun toLisp(): String = "(/ ${numerator.toLisp()} ${denominator.toLisp()})"
}

fun <T> divide(l: Int, r: Int): Expr<T> = divide(integer(l), integer(r))

fun <T> divide(l: Expr<T>, r: Expr<T>): Expr<T> {
    if (r == Zero) return Illegal
    if (l == Zero) return Zero
    if (r == One) return l
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integral && r is Integral) {
        if (l is Integer && r is Integer) return rational(l.n, r.n)
        val ratL = l.toRational() ?: return Illegal
        val ratR = l.toRational() ?: return Illegal
        return rational(
            numerator = ratL.numerator * ratR.denominator,
            denominator = ratL.denominator * ratR.numerator,
        )
    }
    return when {
        l is Scale && r is Scale -> Scale(
            factor = divide(l.factor, r.factor),
            expr = divide(l.expr, r.expr),
        ) as Expr<T>

        l is XPow && r is XPow -> xPow(add(l.pow, multiply(NegOne, r.pow))) as Expr<T>

        l == r -> One
        else -> Divide(l, r)
    }
}
