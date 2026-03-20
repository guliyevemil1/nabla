package org.guliyevemil1.nabla.math

data class Log<T>(val base: Expr<T>) : Expr<T> {
    override val isSimple = true
    override fun render(): String = """\log\left(${base.render()}\right)"""
    override fun toLisp(): String = "(log ${base.toLisp()})"
}

fun <T> log(c: Expr<T>): Expr<T> {
    if (c == One) return Zero
    return when (c) {
        is Integral if c.isNonPositive == Bool.True -> Bottom
        is Bottom -> Bottom
        is ExpX -> X as Expr<T>
        is XPow -> multiply(c.pow, Log(X)) as Expr<T>
        is Scale -> add(log(c.factor), log(c.expr)) as Expr<T>
        is Multiply -> add(c.multiplicants.map { log(it) })
        is Divide -> add(log(c.numerator), negate(log(c.denominator))) as Expr<T>
        else -> Log(c)
    }
}
