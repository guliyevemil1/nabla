package org.guliyevemil1.nabla.math

data class Integrate(val base: Expr<Any?>) : Expr<Any?> {
    override val isConstant: Boolean = base == Zero
    override fun render(): String = """\displaystyle\int{${base.render()}} dx"""
    override fun toLisp(): String = "(integrate ${base.toLisp()})"
    override fun matches(other: Expr<*>): Boolean = other is Integrate &&
            base.matches(other.base)
}

fun integrate(b: Expr<Any?>): Expr<Any?> = when (b) {
    Bottom -> Bottom
    is Constant -> multiply(b, X)
    CosX -> SinX
    SinX -> Scale(NegOne, CosX)
    is Exp -> {
        if (b.pow == X) return b
        Integrate(b)
    }

    is Add -> b.map { integrate(it) }
    is Differentiate -> b.base

    is Divide<Any?> if b.numerator == One && b.denominator == X -> Log(X)
    is Divide<Any?> -> Integrate(b)

    is Integrate -> Integrate(b)
    is Invert -> TODO()
    is Log -> Integrate(b)
    is Multiply<*> -> Integrate(b)
    is Pow -> Integrate(b)

    is Scale -> scale(b.factor, integrate(b.expr))

    is XPow -> {
        if (b.pow == NegOne) return Log(X)
        if (b.pow is Integral) {
            val p = add(b.pow, One)
            if (p == Bottom) return Bottom
            return multiply(divide(One, p as Integral), xPow(p))
        }
        val p = add(b.pow, One)
        multiply(divide(One, p), xPow(p))
    }

    is ConstantMatcher -> Scale(b, X)
    is Matcher -> TODO()
}
