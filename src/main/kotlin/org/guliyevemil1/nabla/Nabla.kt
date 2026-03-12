package org.guliyevemil1.nabla

import kotlin.math.abs

sealed interface Base {
    fun differentiate(): Base
    fun sqrt(): Base = Illegal
    fun inverse(): Base = Illegal
    fun log(): Base = Illegal
    fun lim0(): Constant
    fun limInf(): Constant = Illegal
    fun limNegInf(): Constant = Illegal
    fun limsupInf(): Constant = Illegal
    fun liminfInf(): Constant = Illegal

    fun add(base: Base): Base = Add(listOf(this, base))
    fun subtract(base: Base): Base = add(this, multiply(Constant(-1), base))
    fun multiply(base: Base): Base = Multiply(this, base)
}

object Illegal : Constant {
    override fun differentiate(): Base = Illegal
    override fun lim0(): Constant = Illegal
}

val Zero = Integer(0)
val One = Integer(1)
val Two = Integer(2)
val NegOne = Integer(-1)

class Integer(val n: Int) : Constant {
    override fun isPositive(): Boolean = n > 0
    override fun isZero(): Boolean = n == 0
}

class Rational private constructor(val numerator: Int, val denominator: Int) : Constant {
    companion object {
        fun rational(numerator: Int, denominator: Int): Constant {
            if (numerator < 0 && denominator < 0)
                return rational(-numerator, -denominator)
            if (numerator < 0 || denominator < 0)
                return rational(
                    -abs(numerator),
                    abs(denominator),
                )
            if (denominator == 0) return Illegal
            if (numerator == 0) return Zero
            if (denominator == 1) return Integer(numerator)
            val g = gcd(numerator, denominator)
            if (g == 1) return Rational(numerator, denominator)
            return rational(numerator / g, denominator / g)
        }

        fun gcd(a: Int, b: Int): Int {
            return  gcdInner(abs(a), abs( b))
        }

        private fun gcdInner(a: Int, b: Int): Int {
            return if (b == 0) a else gcdInner(b, a % b)
        }
    }

    override fun isPositive(): Boolean = numerator > 0
    override fun isZero(): Boolean = numerator == 0
}

interface Constant : Base {
    fun isPositive(): Boolean
    fun isZero(): Boolean
    fun isNegative(): Boolean = !isZero() && !isPositive()
    fun isNonPositive(): Boolean = !isPositive()
    fun isNonNegative(): Boolean = !isNegative()

    override fun differentiate(): Base = Zero
    override fun log(): Base =
        if (isNonPositive()) { Illegal } else { Log(this) }
    override fun sqrt(): Base =
        if (isNonPositive()) { Illegal } else { Sqrt(this) }
    override fun inverse(): Base =
        if (isZero()) { Illegal } else { Inverse(this) }

    override fun lim0(): Constant = this
    override fun limInf(): Constant  = this
    override fun limNegInf(): Constant  = this
    override fun limsupInf(): Constant  = this
    override fun liminfInf(): Constant  = this
}

object X : Base {
    override fun differentiate() = One
    override fun lim0(): Constant = Zero
}

val X2 = Multiply(X, X)
object CosX : Base {
    override fun differentiate(): Base = Multiply(NegOne, SinX)
    override fun lim0(): Constant = One
    override fun liminfInf(): Constant = NegOne
    override fun limsupInf(): Constant = One
}

object SinX : Base {
    override fun differentiate(): Base = CosX
    override fun lim0(): Constant = Zero
    override fun liminfInf(): Constant = NegOne
    override fun limsupInf(): Constant = One
}

object ExpX : Base {
    override fun differentiate(): Base = this
    override fun lim0(): Constant = One
    override fun limNegInf(): Constant = Zero
    override fun log(): Base = X
}

class Differentiate(val base: Base) : Base {
    override fun differentiate() = Differentiate(this)
    override fun lim0(): Constant = Illegal
}

class Integrate(val base: Base) : Base {
    override fun differentiate(): Base  = base
    override fun lim0(): Constant = Illegal
}

fun add(l: Base, r: Base): Base = l.add(r)

fun addConstants(summands: List<Constant>): Constant = when (summands.size) {
    0 -> Zero
    1 -> summands.first()
    else -> summands.reduce(::add)
}

fun add(summands: List<Base>): Base = when (summands.size) {
    0 -> Zero
    1 -> summands.first()
    else -> summands.reduce(::add)
}

class Add(val summands: List<Base>) : Base {
    override fun differentiate(): Base = add(summands.map { it.differentiate() })
    override fun lim0(): Constant = add(summands.map { it.lim0() })
}

fun multiply(l: Base, r: Base): Base = l.multiply(r)
class Multiply(val l: Base, val r: Base) : Base {
    override fun differentiate(): Base = Add(
        multiply(l.differentiate(), r),
        multiply(l, r.differentiate()),
    )
    override fun lim0(): Constant = add(l.lim0(), r.lim0())
}

fun sqrt(base: Base): Base = Sqrt(base)

class Sqrt(val base: Base) : Base {
    override fun differentiate(): Base = multiply(base, sqrt(base))
    override fun lim0(): Constant = sqrt(base.lim0())
}

class Log(val base: Base) : Base {
    override fun differentiate(): Base = Log(base)
    override fun lim0(): Constant = base.lim0().log()
}

class Invert(val base: Base) : Base {
    override fun differentiate(): Base = Invert(base)
}

class Inverse(val base: Base) : Base {
    override fun differentiate(): Base = Invert(base)
    override fun lim0(): Constant = base.lim0().inverse()
}
