package org.guliyevemil1.nabla.math

data class Divide<T> constructor(
    val numerator: Expr<T>,
    val denominator: Expr<T>,
) : Expr<T> {
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

private fun <T> inverse(e: Expr<T>): Expr<T> = when {
    e is Exp -> Exp(negate(e.pow))
    else -> pow(e, integer(-1))
}

fun <T> divide(l: Expr<T>, r: Expr<T>): Expr<T> = when {
    l == Bottom || r == Bottom -> Bottom
    l == r -> One
    r == Zero -> Bottom
    l == Zero -> Zero
    r == One -> l

    l is Bottom || r is Bottom -> Bottom
    l is Integral && r is Integral -> {
        divide(l, r)
    }

    else -> multiply(l, inverse(r))
}
