package org.guliyevemil1.nabla.math

data class Integrate(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String = """\int{${base.render()}}"""
}

fun integrate(b: Expr<Any?>): Expr<Any?> = when (b) {
    Illegal -> Illegal
    is Constant -> multiply(b, X)
    CosX -> SinX
    SinX -> Scale(NegOne, CosX)
    ExpX -> ExpX
    is Add -> b.map { integrate(it) }
    is Differentiate -> b.base

    is Divide<*> -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply<*> -> TODO()
    is Sqrt -> TODO()
    is Pow -> TODO()

    is Scale -> {
        multiply(b.factor, integrate(b.expr))
    }

    is XPow -> {
        val p = add(b.pow, One)
        multiply(divide(One, p), xPow(p))
    }
}
