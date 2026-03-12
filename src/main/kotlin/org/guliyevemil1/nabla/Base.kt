package org.guliyevemil1.nabla

sealed interface Base {
    fun toConstant(): Constant = this as? Constant ?: Illegal
}

val Start = listOf(
    One,
    X,
    X2
)

object X : Base

object X2 : Multiply(X, X)

class Add(val summands: List<Base>) : Base {
    constructor(vararg summands: Base) : this(summands.toList())

    fun map(f: (Base) -> Base): Base =
        Add(summands.map(f))
}

open class Multiply(val summands: List<Base>) : Base {
    constructor(vararg summands: Base) : this(summands.toList())
}

class Divide(val numerator: Base, val denominator: Base) : Base

object CosX : Base

object SinX : Base

object ExpX : Base

class Differentiate(val base: Base) : Base

class Integrate(val base: Base) : Base

class Sqrt(val base: Base) : Base

class Log(val base: Base) : Base

class Invert(val base: Base) : Base
