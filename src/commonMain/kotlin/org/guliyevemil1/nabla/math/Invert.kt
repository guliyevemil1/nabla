package org.guliyevemil1.nabla.math

data class Invert(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String =
        TODO("Not yet implemented")

    override fun toLisp(): String {
        TODO("Not yet implemented")
    }
}

fun invert(b: Expr<Any?>): Expr<Any?> = when (b) {
    Illegal -> Illegal
    is Constant -> Illegal
    is CosX -> Illegal
    X -> X

    is Add -> Illegal
    is Divide -> Illegal
    is Multiply -> Illegal
    SinX -> Illegal

    ExpX -> Log(X)
    is Log -> TODO()

    is Invert -> b.base
    is Sqrt -> TODO()

    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Pow -> TODO()
    is Scale -> Scale(divide(One, b.factor), integrate(b.expr))
    is XPow -> TODO()
}
