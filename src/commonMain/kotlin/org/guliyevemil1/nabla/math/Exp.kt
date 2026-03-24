package org.guliyevemil1.nabla.math

data class Exp<T>(val pow: Expr<T>) : Expr<T> {
    override val isConstant = pow.isConstant
    override val isSimple = true

    override fun render(): String = "e^{${pow.render()}}"

    override fun toLisp(): String = "(exp ${pow.toLisp()})"
}

fun <T> e(base: Expr<T>): Expr<T> =
    when (base) {
        is Log<T> -> base.base
        else -> Exp(base)
    }
