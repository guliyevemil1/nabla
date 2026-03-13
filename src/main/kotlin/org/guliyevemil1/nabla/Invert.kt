package org.guliyevemil1.nabla

fun invert(b: Expr<Base<*>>): Expr<Base<*>> = when (b) {
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
    is Sqrt -> Multiply(b.base, b.base)

    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Pow -> TODO()
}
