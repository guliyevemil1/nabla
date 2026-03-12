package org.guliyevemil1.nabla

import org.guliyevemil1.nabla.X2.l

private enum class Limit {
    Zero,
    Infinity,
    NegativeInfinity,
}

private fun lim(b: Base, x: Limit): Constant =
    when (b) {
        is Constant -> b
        is Add -> add(b.summands.map { lim(it, x) })
        is Multiply -> multiply(lim(b.l, x), lim(b.r, x))
        is Divide -> TODO()
        is Sqrt -> sqrt(lim(b.base, x))
        is Log -> log(lim(b.base, x))

        is Differentiate -> TODO()
        is Integrate -> TODO()
        is Invert -> TODO()

        else -> when (x) {
            Limit.Zero -> lim0(b)
            Limit.Infinity -> limInf(b)
            Limit.NegativeInfinity -> limNegInf(b)
        }
    }

fun lim0(b: Base): Constant = when (b) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    is Constant -> b

    is Add -> add(b.summands.map { lim0(it) })

    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> log(lim0(b.base))
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))
    is Divide -> TODO()
}

fun limInf(b: Base): Constant = when (b) {
    CosX -> Illegal
    SinX -> Illegal
    ExpX -> Illegal
    X -> Illegal
    is Constant -> b

    is Add -> add(b.summands.map { lim0(it) })
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))
    is Divide -> TODO()
}

fun limNegInf(b: Base): Constant = when (b) {
    CosX -> Illegal
    SinX -> Illegal
    ExpX -> Illegal
    X -> Illegal
    is Constant -> b
    is Log -> Illegal

    is Add -> add(b.summands.map { lim0(it) })
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Multiply -> multiply(limNegInf(b.l), limNegInf(b.r))
    is Sqrt -> sqrt(limNegInf(b.base))
    is Divide -> TODO()
}

fun limsupInf(b: Base): Constant = when (b) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    is Constant -> b

    is Add -> add(b.summands.map { lim0(it) })
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))
    is Divide -> TODO()
}

fun liminfInf(b: Base): Constant = when (b) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    is Constant -> b

    is Add -> add(b.summands.map { lim0(it) })
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Invert -> TODO()
    is Log -> TODO()
    is Multiply -> multiply(lim0(b.l), lim0(b.r))
    is Sqrt -> sqrt(lim0(b.base))
    is Divide -> TODO()
}
