package org.guliyevemil1.nabla.math

enum class Limit {
    Zero,
    Infinity,
    NegativeInfinity,

    Supremum,
    Infimum,
}

fun lim(b: Expr<Any?>, x: Limit): Expr<Nothing> =
    when (b) {
        Illegal -> Illegal
        is Constant -> b
        is Add -> b.map { lim(it, x) }
        is Multiply -> b.map { lim(it, x) }
        is Divide -> divide(lim(b.numerator, x), lim(b.denominator, x))
        is Sqrt -> sqrt(lim(b.base, x))
        is Log -> log(lim(b.base, x))
        is Pow -> pow(lim(b.base, x), b.pow)

        is Differentiate -> TODO()
        is Integrate -> TODO()
        is Invert -> TODO()

        CosX -> when (x) {
            Limit.Zero -> One
            Limit.Infinity -> Illegal
            Limit.NegativeInfinity -> Illegal
            Limit.Supremum -> One
            Limit.Infimum -> NegOne
        }

        ExpX -> when (x) {
            Limit.Zero -> One
            Limit.Infinity -> Illegal
            Limit.NegativeInfinity -> Zero
            Limit.Supremum -> Illegal
            Limit.Infimum -> Illegal
        }

        SinX -> when (x) {
            Limit.Zero -> Zero
            Limit.Infinity -> Illegal
            Limit.NegativeInfinity -> Illegal
            Limit.Supremum -> One
            Limit.Infimum -> NegOne
        }

        X -> when (x) {
            Limit.Zero -> Zero
            Limit.Infinity -> Illegal
            Limit.NegativeInfinity -> Illegal
            Limit.Supremum -> Illegal
            Limit.Infimum -> Illegal
        }

        is XPow -> when (x) {
            Limit.Zero -> Zero
            Limit.Infinity -> Illegal
            Limit.NegativeInfinity -> Illegal
            Limit.Supremum -> Illegal
            Limit.Infimum -> Illegal
        }

        is Scale -> {
            multiply(b.factor, lim(b.expr, x))
        }
    }
