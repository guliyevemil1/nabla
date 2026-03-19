package org.guliyevemil1.nabla.math

data class Pow<T>(val base: Expr<T>, val pow: Int) : Expr<T> {
    override fun render(): String {
        return when {
            base.isSimple -> """${base.render()}^$pow"""
            base is CosX -> """\cos^$pow x"""
            base is SinX -> """\sin^$pow x"""
            else -> """\left(${base.render()}\right)^$pow"""
        }
    }
}

private val xPowMap = HashMap<Integer, XPow>()

fun xPow(pow: Expr<Nothing>): Expr<Any?> {
    if (pow == Zero) return One
    if (pow is Integer) xPowMap[pow] ?: XPow(pow).also { xPowMap[pow] = it }
    return XPow(pow)
}

data class XPow(val pow: Expr<Nothing>) : Expr<Any?> {
    override fun render(): String {
        if (pow == One) {
            return "x"
        }
        if (pow is Rational && pow.denominator == 2) {
            if (pow.numerator == 1) return """\sqrt{x}"""
            return """\sqrt{x^${pow.numerator}}"""
        }
        return """x^{${pow.render()}}"""
    }
}

fun <T> pow(base: Expr<T>, n: Int): Expr<T> {
    if (base is XPow) {
        return xPow(multiply(base.pow, integer(n))) as Expr<T>
    }
    if (n < 0) return Illegal
    if (n == 0) return One
    return multiply(base, pow(base, n - 1))
}
