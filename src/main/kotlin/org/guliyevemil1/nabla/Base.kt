package org.guliyevemil1.nabla


sealed interface Base {
    fun toConstant(): Constant = this as? Constant ?: Illegal
}

object X : Base

object X2 : Multiply(X, X)

object CosX : Base

object SinX : Base

object ExpX : Base

class Differentiate(val base: Base) : Base

class Integrate(val base: Base) : Base

class Sqrt(val base: Base) : Base

class Log(val base: Base) : Base

class Invert(val base: Base) : Base

class Inverse(val base: Base) : Base
