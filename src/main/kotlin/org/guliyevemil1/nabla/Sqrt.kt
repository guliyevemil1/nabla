package org.guliyevemil1.nabla

fun sqrt(c: Int): Int = TODO()

fun sqrt(c: Constant): Constant {
    if (c.isNegative()) {
        return Illegal
    }
    if (c.isZero()) {
        return Zero
    }
    return when (c) {
        is Illegal -> Illegal
        is Integer -> c
        is Rational -> c
    }
}
