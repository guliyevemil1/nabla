package org.guliyevemil1.nabla.math

data class Sqrt<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\sqrt{${base.render()}}"""
}

fun <T> sqrt(c: Expr<T>): Expr<T> {
    if (c is Constant) {
        if (c == Zero || c == One) return c
        return Sqrt(c)
    }
    if (c is XPow) {
        return xPow(divide(c.pow, integer(2))) as Expr<T>
    }
    if (c is Multiply && c.multiplicants.size == 2 && c.multiplicants[0] == c.multiplicants[1]) {
        return c.multiplicants[0]
    }
    return Sqrt(c)
}
