package org.guliyevemil1.nabla

fun lim0(b: Base): Constant = when (b) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    Illegal -> Illegal
    is Add -> add(b.summands.map { lim0(it) })
    is Integer -> b
    is Rational -> b
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Inverse -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))
}

fun limInf(c: Base): Constant = when (c) {
    CosX -> Illegal
    SinX -> Illegal
    ExpX -> Illegal
    X -> Illegal
    Illegal -> Illegal
    is Add -> add(b.summands.map { lim0(it) })
    is Integer -> b
    is Rational -> b
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Inverse -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))
}

fun limNegInf(c: Base): Constant = when (c) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    Illegal -> Illegal
    is Add -> add(b.summands.map { lim0(it) })
    is Integer -> b
    is Rational -> b
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Inverse -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))

}

fun limsupInf(c: Base): Constant = when (c) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    Illegal -> Illegal
    is Add -> add(b.summands.map { lim0(it) })
    is Integer -> b
    is Rational -> b
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Inverse -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))

}

fun liminfInf(c: Base): Constant = when (c) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    Illegal -> Illegal
    is Add -> add(b.summands.map { lim0(it) })
    is Integer -> b
    is Rational -> b
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Inverse -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))

}
