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
        numerator = ratL.numerator * ratR.denominator,
        denominator = ratL.denominator * ratR.numerator,
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

        l == One -> {
            if (r is XPow) {
                xPow(negate(r.pow)) as Expr<T>
            } else {
                Divide(One, r)
            }
        }

        l is Add -> l.map { divide(it, r) }

        l is Divide -> Divide(
            l.numerator,
            multiply(l.denominator, r),
        )

        l is Scale && r is Scale -> scale(
            factor = divide(l.factor, r.factor),
            expr = divide(l.expr, r.expr),
        ) as Expr<T>

        l is XPow && r is XPow -> xPow(add(l.pow, multiply(NegOne, r.pow))) as Expr<T>

        else -> Divide(l, r)
    }
