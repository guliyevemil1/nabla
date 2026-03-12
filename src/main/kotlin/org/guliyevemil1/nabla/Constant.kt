package org.guliyevemil1.nabla

import kotlin.math.abs

val Zero = Integer(0)
val One = Integer(1)
val Two = Integer(2)
val NegOne = Integer(-1)

sealed interface Constant : Base {
    fun isPositive(): Boolean
    fun isZero(): Boolean
    fun isNegative(): Boolean = !isZero() && !isPositive()
    fun isNonPositive(): Boolean = !isPositive()
    fun isNonNegative(): Boolean = !isNegative()

    fun toRational(): Rational?
    fun inverse(): Constant
}

object Illegal : Constant {
    override fun isPositive(): Boolean = false
    override fun isZero(): Boolean = false
    override fun toRational(): Rational? = null
    override fun inverse() = Illegal
}

class Integer(val n: Int) : Constant {
    override fun isPositive(): Boolean = n > 0
    override fun isZero(): Boolean = n == 0
    override fun toRational() = Rational(n, 1)

    override fun inverse(): Constant =
        if (!isZero()) Rational(1, n) else Illegal
}

fun gcd(a: Int, b: Int): Int {
    return gcdInner(abs(a), abs(b))
}

private fun gcdInner(a: Int, b: Int): Int {
    return if (b == 0) a else gcdInner(b, a % b)
}

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

class Rational internal constructor(val numerator: Int, val denominator: Int) : Constant {
    override fun isPositive(): Boolean = numerator > 0
    override fun isZero(): Boolean = numerator == 0

    override fun inverse(): Constant =
        if (!isZero()) rational(denominator, numerator)
        else Illegal

    override fun toRational() = this
}

fun constExpr(b: Base): Constant =
    when (b) {
        Illegal -> b
        is Constant -> b

        X -> Illegal
        CosX -> Illegal
        ExpX -> Illegal
        SinX -> Illegal

        is Add -> TODO()
        is Differentiate -> TODO()
        is Integrate -> TODO()

        is Invert -> TODO()
        is Log -> TODO()
        is Multiply -> TODO()
        is Sqrt -> TODO()
        is Divide -> TODO()
    }

class ConstExpr private constructor(b: Base) : Constant {
    override fun isPositive(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isZero(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toRational(): Rational? = null

    override fun inverse(): Constant {
        TODO("Not yet implemented")
    }
}
