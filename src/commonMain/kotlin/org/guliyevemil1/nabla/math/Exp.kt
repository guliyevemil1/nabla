package org.guliyevemil1.nabla.math

data class Exp<T>(val base: Expr<T>) : Expr<T> {
    override val isConstant = base.isConstant

    override fun render(): String = "e^{${base.render()}}"

    override fun toLisp(): String = "(exp ${base.toLisp()})"
}
