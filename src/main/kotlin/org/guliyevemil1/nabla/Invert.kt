package org.guliyevemil1.nabla

fun invert(b: Expr<Any?>): Expr<Any?> = when (b) {
    Illegal -> Illegal
    is Constant -> Illegal
    is CosX -> Illegal
    X -> X

    is Add -> Illegal
    is Divide -> Illegal
    is Multiply -> Illegal
    SinX -> Illegal

    ExpX -> Log(X)
    is Log -> when (b.base) {
        is X -> ExpX
        else -> Illegal
    }

    is Invert -> b.base
    is Sqrt -> TODO()

    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Pow -> TODO()
}
