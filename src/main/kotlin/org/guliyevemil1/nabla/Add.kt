package org.guliyevemil1.nabla

fun add(l: Constant, r: Constant): Constant {
    if (l is Integer && r is Integer) {
        return Integer(l.n + r.n)
    }
    if (l is Illegal || r is Illegal) {
        return Illegal
    }
    val ratL = l.toRational()
    val ratR = l.toRational()

}

fun add(l: Base, r: Base): Base = l.add(r)

fun add(summands: List<Base>): Base = when (summands.size) {
    0 -> Zero
    1 -> summands.first()
    else -> summands.reduce(::add)
}

class Add(val summands: List<Base>) : Base {
    override fun lim0(): Constant = add(summands.map { it.lim0() })
}

fun multiply(l: Base, r: Base): Base = l.multiply(r)
open class Multiply(val l: Base, val r: Base) : Base {
    override fun lim0(): Constant = add(l.lim0(), r.lim0())
}

fun multiply(l: Constant, r: Constant): Constant = TODO()
