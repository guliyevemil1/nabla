package org.guliyevemil1.nabla.math

data class Divide<T>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T> {
    override val isConstant: Boolean = numerator.isConstant && denominator.isConstant

    override fun render(): String =
        """\frac{${numerator.render()}}{${denominator.render()}}"""

    override fun toLisp(): String = "(/ ${numerator.toLisp()} ${denominator.toLisp()})"
}

fun divide(l: Integral, r: Integral): Expr<Nothing> {
    if (l is Integer && r is Integer) return rational(l.n, r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    return rational(
        numerator = ratL.numerator * ratR.numerator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun <T> divide(l: Expr<T>, r: Expr<T>): Expr<T> =
    when {
        l == Bottom || r == Bottom -> Bottom
        l == r -> One
        r == Zero -> Bottom
        l == Zero -> Zero
        r == One -> l

        l is Bottom || r is Bottom -> Bottom
        l is Integral && r is Integral -> {
            divide(l, r)
        }

        l.isConstant -> scale(l as Expr<Nothing>, Divide(One, r))

        l is Add -> l.map { divide(it, r) }

        l is Divide -> Divide(
            l.numerator,
            multiply(l.denominator, r),
        )

        l is Scale && r is Scale -> scale(
            factor = divide(l.factor, r.factor),
            expr = divide(l.expr, r.expr),
        )

        l is Scale -> scale(
            factor = l.factor,
            expr = divide(l.expr, r),
        )

        r is Scale -> scale(
            factor = r.factor,
            expr = divide(l, r.expr),
        )

        l is XPow && r is XPow -> xPow(add(l.pow, multiply(NegOne, r.pow))) as Expr<T>

        l is Pow && r is Pow && l.base == r.base -> pow(l.base, add(l.pow, multiply(NegOne, r.pow)))
        l is Pow && l.base == r -> pow(l.base, add(l.pow, NegOne))

        else -> Divide(l, r)
    }
