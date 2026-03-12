package org.guliyevemil1.nabla

sealed interface Base {
    fun toConstant(): Constant = this as? Constant ?: Illegal
}

object X : Base

object X2 : Multiply(X, X)

fun Add(l: Base, r: Base): Add = Add(
    listOf(l, r)
)

class Add(val summands: List<Base>) : Base {
    fun map(f: (Base) -> Base): Base =
        Add(summands.map(f))
}

open class Multiply(val l: Base, val r: Base) : Base

class Divide(val numerator: Base, val denominator: Base) : Base

object CosX : Base

object SinX : Base

object ExpX : Base

class Differentiate(val base: Base) : Base

class Integrate(val base: Base) : Base

class Sqrt(val base: Base) : Base

class Log(val base: Base) : Base

class Invert(val base: Base) : Base
