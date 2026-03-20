package org.guliyevemil1.nabla.math

data class Integrate(val base: Expr<Any?>) : Expr<Any?> {
    override val isConstant: Boolean = base == Zero
    override fun render(): String = """\int{${base.render()}}"""
    override fun toLisp(): String = "(integrate ${base.toLisp()})"
}

fun integrate(b: Expr<Any?>): Expr<Any?> = when (b) {
    Bottom -> Bottom
    is Constant -> multiply(b, X)
    CosX -> SinX
    SinX -> Scale(NegOne, CosX)
    ExpX -> ExpX
    is Add -> b.map { integrate(it) }
    is Differentiate -> b.base

    is Divide<Any?> if b.numerator == One && b.denominator == X -> Log(X)
    is Divide<Any?> -> TODO()

    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply<*> -> TODO()
    is Sqrt -> TODO()
    is Pow -> TODO()

    is Scale -> {
        Scale(b.factor, integrate(b.expr))
    }

    is XPow -> {
        val p = add(b.pow, One)
        multiply(divide(One, p), xPow(p))
    }
}
