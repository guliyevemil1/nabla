package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.util.splitBy

data class Divide<T>(
    val numerator: Expr<T>,
    val denominator: Expr<T>,
) : Expr<T> {
    override val isConstant: Boolean = numerator.isConstant && denominator.isConstant

    override fun render(): String =
        """\frac{${numerator.render()}}{${denominator.render()}}"""

    override fun toLisp(): String = "(/ ${numerator.toLisp()} ${denominator.toLisp()})"
    fun simplify(): Expr<T> {
        val ns = listOf(numerator).flattenMultiply()
        val ds = listOf(denominator).flattenMultiply().map { pow(it, integer(-1)) }
        return (ns + ds)
            .sortedWith(ExprComparator)
            .foldMultiply()
            .let { e ->
                if (e is Multiply) {
                    val (n, d) = e
                        .multiplicants
                        .splitBy {
                            when (it) {
                                is Pow if it.pow.isNegative == Bool.True -> false
                                is Exp if it.pow is Scale && it.pow.factor.isNegative == Bool.True -> false
                                else -> true
                            }
                        }
                    val nn = multiply(n)
                    val dd = multiply(d.map { pow(it, integer(-1)) })
                    if (dd == One) nn else Divide(nn, dd)
                } else {
                    e
                }
            }
    }
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
