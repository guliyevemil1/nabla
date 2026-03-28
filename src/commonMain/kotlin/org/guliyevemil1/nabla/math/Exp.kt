package org.guliyevemil1.nabla.math

data class Exp<T> constructor(val pow: Expr<T>) : Expr<T> {
    override val isConstant = pow.isConstant
    override val isSimple = true

    override fun render(): String = "e^{${pow.render()}}"

    override fun toLisp(): String = "(exp ${pow.toLisp()})"
    override fun matches(other: Expr<*>): Boolean = other is Exp &&
            pow.matches(other.pow)
}

fun <T> e(base: Expr<T>): Expr<T> =
    when (base) {
        Zero -> One
        is Log<T> -> base.base
        else -> Exp(base)
    }
