package org.guliyevemil1.nabla

fun Base.differentiate(): Base {
    when (this) {
        is Integer -> Zero
        is Rational -> Zero
        is Add -> this.summands.differentiate()
        is Multiply -> TODO()
        is Differentiate -> Differentiate(this)
        is SinX -> CosX
        is CosX -> Multiply(NegOne, SinX)
        is ExpX -> ExpX
        is Integrate -> this.base
        is Inverse -> TODO()
        is Invert -> TODO()
        is Log -> TODO()
        is Sqrt -> TODO()
        is X -> One
        is Illegal -> Illegal
    }
}

fun List<Base>.differentiate(): Base =
    add(map { it.differentiate() })
}
