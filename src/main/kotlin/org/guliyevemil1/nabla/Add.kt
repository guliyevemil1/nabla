package org.guliyevemil1.nabla

fun add(l: Constant, r: Constant): Constant {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return Integer(l.n + r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    return rational(
        numerator = ratL.numerator * ratR.denominator + ratR.numerator * ratL.denominator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun add(summands: List<Constant>): Constant {
    return when (summands.size) {
        0 -> return Zero
        1 -> summands[0]
        else -> summands.reduce(::add)
    }
}

class Add(val summands: List<Base>) : Base
open class Multiply(val l: Base, val r: Base) : Base

fun multiply(l: Constant, r: Constant): Constant {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return Integer(l.n * r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    return rational(
        numerator = ratL.numerator * ratR.numerator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun multiply(summands: List<Constant>): Constant {
    return when (summands.size) {
        0 -> return Zero
        1 -> summands[0]
        else -> summands.reduce(::multiply)
    }
}
