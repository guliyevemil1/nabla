package org.guliyevemil1.nabla.math

data class Log<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\log\left(${base.render()}\right)"""
}

fun <T> log(c: Expr<T>): Expr<T> {
    if (c == One) return Zero
    return when (c) {
        is Integral if c.isNonPositive == Bool.True -> Illegal
        is Illegal -> Illegal
        is ExpX -> X as Expr<T>
        is XPow -> multiply(c.pow, Log(X)) as Expr<T>
        is Scale -> add(log(c.factor), log(c.expr)) as Expr<T>
        is Multiply -> add(c.multiplicants.map { log(it) })
        else -> Log(c)
    }
}
