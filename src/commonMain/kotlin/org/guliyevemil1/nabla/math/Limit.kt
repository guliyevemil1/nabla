package org.guliyevemil1.nabla.math

enum class Limit {
    Zero,
    Infinity,
    NegativeInfinity,

    Supremum,
    Infimum,
}

private object Infinity : Constant {
    override fun render(): String = """\infty"""
    override fun toLisp(): String = """inf"""
}

private object NegativeInfinity : Constant {
    override fun render(): String = """-\infty"""
    override fun toLisp(): String = """-inf"""
}

private fun limInner(b: Expr<Any?>, x: Limit): Expr<Nothing> =
    when (b) {
        Bottom -> Bottom
        is Constant -> b
        is Add -> b.map { lim(it, x) }
        is Multiply -> b.map { lim(it, x) }
        is Divide -> divide(lim(b.numerator, x), lim(b.denominator, x))
        is Log -> log(lim(b.base, x))
        is Pow -> pow(lim(b.base, x), b.pow)

        is Differentiate -> TODO()
        is Integrate -> TODO()
        is Invert -> TODO()

        CosX -> when (x) {
            Limit.Zero -> One
            Limit.Infinity -> Bottom
            Limit.NegativeInfinity -> Bottom
            Limit.Supremum -> One
            Limit.Infimum -> NegOne
        }

        SinX -> when (x) {
            Limit.Zero -> Zero
            Limit.Infinity -> Bottom
            Limit.NegativeInfinity -> Bottom
            Limit.Supremum -> One
            Limit.Infimum -> NegOne
        }

        is XPow -> when (x) {
            Limit.Zero -> {
                if (b.pow is Integer) {
                    return when {
                        b.pow.n == 0 -> One
                        b.pow.n < 0 && b.pow.n % 2 == 0 -> Infinity
                        b.pow.n < 0 -> Bottom
                        else -> Zero
                    }
                }
                TODO()
            }

            Limit.Infinity -> {
                if (b.pow is Integer) {
                    return when {
                        b.pow.n == 0 -> One
                        b.pow.n < 0 -> Bottom
                        b.pow.n % 2 == 0 -> Infinity
                        else -> NegativeInfinity
                    }
                }
                TODO()
            }

            Limit.NegativeInfinity -> {
                if (b.pow is Integer) {
                    return when {
                        b.pow.n == 0 -> One
                        b.pow.n < 0 -> Bottom
                        b.pow.n % 2 == 0 -> Infinity
                        else -> NegativeInfinity
                    }
                }
                TODO()
            }

            Limit.Supremum -> Infinity
            Limit.Infimum -> Infinity
        }

        is Scale -> {
            val result = when (x) {
                Limit.Supremum if b.factor.isNegative == Bool.True -> lim(b.expr, Limit.Infimum)
                Limit.Infimum if b.factor.isNegative == Bool.True -> lim(b.expr, Limit.Supremum)
                else -> lim(b.expr, x)
            }
            multiply(b.factor, result)
        }

        is Exp<*> -> TODO()
    }

fun lim(b: Expr<Any?>, x: Limit): Expr<Nothing> = limInner(b, x).let {
    if (it == Infinity || it == NegativeInfinity)
        Bottom
    else
        it
}
