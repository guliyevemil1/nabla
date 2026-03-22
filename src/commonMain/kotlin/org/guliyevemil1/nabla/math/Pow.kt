package org.guliyevemil1.nabla.math

fun <T> pow(base: Expr<T>, pow: Expr<Nothing>): Expr<T> {
    if (pow == Bottom) return Bottom
    if (pow == Zero) return One
    if (pow == One) return base
    if (base is XPow) return xPow(multiply(base.pow, pow)) as Expr<T>
    return Pow(base, pow)
}

data class Pow<T>(val base: Expr<T>, val pow: Expr<Nothing>) : Expr<T> {
    override val isSimple: Boolean = base.isSimple
    override val isConstant: Boolean = base.isConstant

    override fun render(): String {
        if (pow is Rational && pow.denominator == 2) return """\sqrt{${pow(base, integer(pow.numerator)).render()}}"""
        return when (base) {
            is CosX -> """\cos^{${pow.render()}} x"""
            is SinX -> """\sin^{${pow.render()}} x"""
            else -> """\left(${base.render()}\right)^{${pow.render()}}"""
        }
    }

    override fun toLisp(): String {
        return "(pow ${base.toLisp()} ${pow.toLisp()})"
    }
}

private val xPowMap = HashMap<Int, XPow>()

fun xPow(pow: Int): Expr<Any?> =
    xPowMap[pow] ?: XPow(integer(pow)).also { xPowMap[pow] = it }

fun xPow(pow: Expr<Nothing>): Expr<Any?> {
    return when (pow) {
        Bottom -> Bottom
        Zero -> One
        is Integer -> xPow(pow.n)
        else -> XPow(pow)
    }
}

data class XPow(val pow: Expr<Nothing>) : Expr<Any?> {
    override val isConstant: Boolean = false

    override val isSimple = true
    override fun render(): String {
        if (pow == One) return "x"
        if (pow is Rational && pow.denominator == 2) {
            if (pow.numerator == 1) return """\sqrt{x}"""
            return """\sqrt{x^${pow.numerator}}"""
        }
        return """x^{${pow.render()}}"""
    }

    override fun toLisp(): String {
        if (pow == One) return "x"
        return "(xpow ${pow.toLisp()})"
    }
}
