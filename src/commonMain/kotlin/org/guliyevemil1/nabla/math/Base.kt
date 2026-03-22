package org.guliyevemil1.nabla.math

val X = xPow(One)
val X2 = xPow(integer(2))

val ExpX = Exp(X)

object CosX : Expr<Any?> {
    override val isSimple = true
    override val isConstant: Boolean = false

    override fun render(): String = """\cos(x)"""
    override fun toLisp(): String = "(cos x)"
}

object SinX : Expr<Any?> {
    override val isSimple = true
    override val isConstant: Boolean = false

    override fun render(): String = """\sin(x)"""
    override fun toLisp(): String = "(sin x)"
}
