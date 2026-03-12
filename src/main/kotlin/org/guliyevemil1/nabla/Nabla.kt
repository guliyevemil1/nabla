package org.guliyevemil1.nabla

import org.guliyevemil1.nabla.Rational.Companion.rational
import kotlin.math.abs


sealed interface Base {
    fun sqrt(): Base = Illegal

    fun inverse(): Base = Illegal
    fun log(): Base = Illegal
    fun lim0(): Constant
    fun limInf(): Constant = Illegal
    fun limNegInf(): Constant = Illegal
    fun limsupInf(): Constant = Illegal
    fun liminfInf(): Constant = Illegal

    fun add(base: Base): Base = Add(listOf(this, base))
    fun subtract(base: Base): Base = add(this, multiply(NegOne, base))
    fun multiply(base: Base): Base = Multiply(this, base)
}

object Illegal : Constant {
    override fun isPositive(): Boolean = false
    override fun isZero(): Boolean = false
    override fun lim0(): Constant = Illegal
    override fun addConstant(r: Constant): Constant = Illegal
}

val Zero = Integer(0)
val One = Integer(1)
val Two = Integer(2)
val NegOne = Integer(-1)

class Integer(val n: Int) : Constant {
    override fun isPositive(): Boolean = n > 0
    override fun isZero(): Boolean = n == 0

    override fun addConstant(other: Constant): Constant = when (other) {
        is Integer -> Integer(n + other.n)
        is Rational -> Rational(n, 1).addConstant(other)
        is Illegal -> Illegal
    }

    override fun inverse(): Constant =
        if (isZero()) { Illegal } else { rational(1, n) }
}

class Rational internal constructor(val numerator: Int, val denominator: Int) : Constant {
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
    override fun inverse(): Constant =
        if (isZero()) { Illegal } else { rational(denominator, numerator) }

    override fun addConstant(other: Constant): Constant = when (other) {
        is Integer -> addConstant(Rational(other.n, 1))
        is Rational -> rational(
            numerator * other.denominator + other.numerator * denominator,
            denominator * other.denominator,
        )
        is Illegal -> Illegal
    }
}

sealed interface Constant : Base {
    fun isPositive(): Boolean
    fun isZero(): Boolean
    fun isNegative(): Boolean = !isZero() && !isPositive()
    fun isNonPositive(): Boolean = !isPositive()
    fun isNonNegative(): Boolean = !isNegative()

    override fun log(): Base =
        if (isNonPositive()) { Illegal } else { Log(this) }
    override fun sqrt(): Base =
        if (isNonPositive()) { Illegal } else { Sqrt(this) }

    override fun lim0(): Constant = this
    override fun limInf(): Constant  = this
    override fun limNegInf(): Constant  = this
    override fun limsupInf(): Constant  = this
    override fun liminfInf(): Constant  = this

    fun addConstant(r: Constant): Constant
}

object X : Base {
    override fun lim0(): Constant = Zero
}

object X2 : Multiply(X, X)

object CosX : Base {
    override fun lim0(): Constant = One
    override fun liminfInf(): Constant = NegOne
    override fun limsupInf(): Constant = One
}

object SinX : Base {
    override fun lim0(): Constant = Zero
    override fun liminfInf(): Constant = NegOne
    override fun limsupInf(): Constant = One
}

object ExpX : Base {
    override fun lim0(): Constant = One
    override fun limNegInf(): Constant = Zero
    override fun log(): Base = X
}

class Differentiate(val base: Base) : Base {
    override fun lim0(): Constant = Illegal
}

class Integrate(val base: Base) : Base {
    override fun lim0(): Constant = Illegal
}


fun addConstant(l: Constant, r: Constant): Constant = l.addConstant(r)

fun addConstants(summands: List<Constant>): Constant = when (summands.size) {
    0 -> Zero
    1 -> summands.first()
    else -> summands.reduce(::addConstant)
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

fun sqrt(base: Base): Base = Sqrt(base)

class Sqrt(val base: Base) : Base {
    override fun lim0(): Constant = sqrt(base.lim0())
}

class Log(val base: Base) : Base {
    override fun lim0(): Constant = base.lim0().log()
}

class Invert(val base: Base) : Base {
}

class Inverse(val base: Base) : Base {
    override fun lim0(): Constant = base.lim0().inverse()
}
