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

    override fun toLisp(): String {
        return "(pow ${base.toLisp()} ${pow})"
    }
}

private val xPowMap = HashMap<Int, XPow>()

fun xPow(pow: Int): Expr<Any?> =
    xPowMap[pow] ?: XPow(integer(pow)).also { xPowMap[pow] = it }

fun xPow(pow: Expr<Nothing>): Expr<Any?> {
    return when (pow) {
        Zero -> One
        is Integer -> xPow(pow.n)
        else -> XPow(pow)
    }
}

data class XPow(val pow: Expr<Nothing>) : Expr<Any?> {
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
        return "(xpow ${pow.toLisp()})"
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
