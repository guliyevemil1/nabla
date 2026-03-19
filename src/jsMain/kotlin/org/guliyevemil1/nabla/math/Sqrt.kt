package org.guliyevemil1.nabla.math

data class Sqrt<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\sqrt{${base.render()}}"""
}

fun <T> sqrt(c: Expr<T>): Expr<T> =
    when (c) {
        is Constant if (c == Zero || c == One) -> c
        is Constant -> Sqrt(c)
        is XPow -> xPow(divide(c.pow, integer(2))) as Expr<T>
        is Multiply if c.multiplicants.size == 2 && c.multiplicants[0] == c.multiplicants[1] -> {
            c.multiplicants[0]
        }

        is Multiply -> multiply(c.multiplicants.map { sqrt(it) })
        is Divide -> divide(sqrt(c.numerator), sqrt(c.denominator))

        else -> Sqrt(c)
    }
