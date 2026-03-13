package org.guliyevemil1.nabla

fun integrate(b: Base): Base = when (b) {
    Illegal -> Illegal
    is Constant -> multiply(b, X)
    CosX -> SinX
    SinX -> Multiply(NegOne, CosX)
    ExpX -> ExpX
    is Add -> b.map { integrate(it) }
    is Differentiate -> b.base
    X -> Multiply(rational(1, 2), pow(X, 2))

    is Divide -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> TODO()
    is Sqrt -> TODO()
    is Pow -> TODO()
}
