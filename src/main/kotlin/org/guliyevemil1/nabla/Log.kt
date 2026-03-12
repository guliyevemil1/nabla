package org.guliyevemil1.nabla

fun log(c: Constant): Constant {
    if (c.isNonPositive()) return Illegal
    return ConstExpr(c)
}
