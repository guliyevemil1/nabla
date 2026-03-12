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

class Add(val summands: List<Base>) : Base
open class Multiply(val l: Base, val r: Base) : Base

fun multiply(l: Constant, r: Constant): Constant = TODO()
