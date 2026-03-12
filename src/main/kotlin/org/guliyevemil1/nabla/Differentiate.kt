package org.guliyevemil1.nabla

fun differentiate(b: Base): Base =
    when (b) {
        is Integer -> Zero
        is Rational -> Zero
        is Add -> b.summands.differentiate()
        is Multiply -> TODO()
        is Differentiate -> Differentiate(b)
        is SinX -> CosX
        is CosX -> Multiply(NegOne, SinX)
        is ExpX -> ExpX
        is Integrate -> b.base
        is Inverse -> TODO()
        is Invert -> TODO()
        is Log -> Multiply(Inverse(b.base), differentiate(b.base))
        is Sqrt -> TODO()
        is X -> One
        is Illegal -> Illegal
    }

fun List<Base>.differentiate(): Base =
    Add(map { differentiate(it) })
