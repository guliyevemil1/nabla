package org.guliyevemil1.nabla.math

data class Invert(val base: Expr<Any?>) : Expr<Any?> {
    override val isConstant: Boolean = false

    override fun render(): String =
        TODO("Not yet implemented")

    override fun toLisp(): String =
        "(invert ${base.toLisp()})"

    override fun matches(other: Expr<*>): Boolean = other is Differentiate &&
            base.matches(other.base)
}

fun invert(b: Expr<Any?>): Expr<Any?> = when (b) {
    Bottom -> Bottom
    is Constant -> Bottom
    is CosX -> Bottom
    X -> X

    is Add -> Bottom
    is Divide -> Bottom
    is Multiply -> Bottom
    SinX -> Bottom

    is Exp -> {
        if (b.pow == X) return Log(X)
        TODO()
    }

    is Log -> TODO()

    is Invert -> b.base

    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Pow -> TODO()
    is Scale -> scale(divide(One, b.factor), integrate(b.expr))
    is XPow -> TODO()
    is Matcher<*> -> TODO()
}
