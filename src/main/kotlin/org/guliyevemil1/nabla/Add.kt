package org.guliyevemil1.nabla

fun add(l: Constant, r: Constant): Constant {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return Integer(l.n + r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    if (ratL == null || ratR == null) {
        TODO()
    }
    return rational(
        numerator = ratL.numerator * ratR.denominator + ratR.numerator * ratL.denominator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun add(summands: List<Constant>): Constant {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands.reduce(::add)
    }
}

fun multiply(l: Constant, r: Constant): Constant {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return Integer(l.n * r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    if (ratL == null || ratR == null) {
        TODO()
    }
    return rational(
        numerator = ratL.numerator * ratR.numerator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun divide(l: Constant, r: Constant): Constant {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return rational(l.n, r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    if (ratL == null || ratR == null) {
        TODO()
    }
    return rational(
        numerator = ratL.numerator * ratR.denominator,
        denominator = ratL.denominator * ratR.numerator,
    )
}
