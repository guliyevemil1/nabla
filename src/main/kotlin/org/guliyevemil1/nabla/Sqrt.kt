package org.guliyevemil1.nabla

import org.guliyevemil1.nabla.ConstExpr.Companion.constExpr

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
        else -> constExpr(Sqrt(c))
    }
}
