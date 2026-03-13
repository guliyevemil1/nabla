package org.guliyevemil1.nabla

private enum class Limit {
    Zero,
    Infinity,
    NegativeInfinity,

    Supremum,
    Infimum,
}

private fun lim(b: Base, x: Limit): Constant =
    when (b) {
        is Constant -> b
        is Add -> add(b.summands.map { lim(it, x) })
        is Multiply -> multiply(b.multiplicants.map { lim(it, x) })
        is Divide -> divide(lim(b.numerator, x), lim(b.denominator, x))
        is Sqrt -> sqrt(lim(b.base, x))
        is Log -> log(lim(b.base, x))

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
    }
