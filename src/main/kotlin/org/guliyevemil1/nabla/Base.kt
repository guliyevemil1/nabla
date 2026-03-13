package org.guliyevemil1.nabla

sealed interface Base {
    fun toConstant(): Constant = this as? Constant ?: Illegal
}

object X : Base

class Add(val summands: List<Base>) : Base {
    constructor(vararg summands: Base) : this(summands.asList())

    fun map(f: (Base) -> Constant): Constant =
        add(summands.map(f))

    fun map(f: (Base) -> Base): Base =
        add(summands.map(f))
}

open class Multiply(val multiplicants: List<Base>) : Base {
    constructor(vararg multiplicants: Base) : this(multiplicants.asList())

    fun map(f: (Base) -> Constant): Constant =
        multiply(multiplicants.map(f))

    fun map(f: (Base) -> Base): Base =
        multiply(multiplicants.map(f))
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
