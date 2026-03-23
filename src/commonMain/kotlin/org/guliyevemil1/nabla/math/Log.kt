package org.guliyevemil1.nabla.math

data class Log<T>(val base: Expr<T>) : Expr<T> {
    override val isSimple = true
    override val isConstant: Boolean = base.isConstant

    override fun render(): String = """\log\left(${base.render()}\right)"""
    override fun toLisp(): String = "(log ${base.toLisp()})"
}

fun <T> log(c: Expr<T>): Expr<T> {
    if (c == One) return Zero
    return when (c) {
        is Integral if c.isNonPositive == Bool.True -> Bottom
        is Rational -> add(log(integer(c.numerator)), negate(log(integer(c.denominator))))
        is Bottom -> Bottom
        is Exp -> c.base
        is XPow -> multiply(c.pow, Log(X)) as Expr<T>
        is Scale -> add(log(c.factor), log(c.expr))
        is Multiply -> add(c.multiplicants.map { log(it) })
        is Divide -> add(log(c.numerator), negate(log(c.denominator)))
        else -> Log(c)
    }
}
