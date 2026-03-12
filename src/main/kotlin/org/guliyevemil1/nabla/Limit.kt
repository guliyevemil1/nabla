package org.guliyevemil1.nabla

fun List<Base>.lim0(): List<Constant> =
    map { it.lim0() }

fun Base.lim0(): Constant = when(this) {
    CosX -> One
    SinX -> Zero
    ExpX -> One
    X -> Zero
    is Add -> add(summands.lim0())
    is Integer -> this
    is Rational -> this
    is Differentiate -> TODO()
    is Integrate -> TODO()
    is Inverse -> TODO()
    is Invert -> TODO()
//    is Multiply -> multiply(lim0(this.l) , lim0(this.r))
//    is Sqrt -> sqrt(lim0(this.base))
    else -> Illegal
}

fun limInf(c: Base): Constant = when(c) {

}
fun limNegInf(c: Base): Constant = when(c) {

}
fun limsupInf(c: Base): Constant = when(c) {

}
fun liminfInf(c: Base): Constant = when(c) {

}
