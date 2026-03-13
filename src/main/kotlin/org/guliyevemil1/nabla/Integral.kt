package org.guliyevemil1.nabla

import kotlin.math.abs

sealed interface Integral : Constant {
    fun toRational(): Rational? = null

    override fun inverse(): Expr<Nothing>
}

object Illegal : Expr<Nothing>

private val integerMap = HashMap<Int, Integer>()

fun integer(n: Int): Integer = integerMap.computeIfAbsent(n, ::Integer)

data class Integer(val n: Int) : Integral {
    override val sign: Sign = when {
        n < 0 -> Sign.Negative
        n == 0 -> Sign.Zero
        else -> Sign.Positive
    }

    override fun toRational() = Rational(n, 1)

    override fun inverse(): Expr<Nothing> = when (isZero()) {
        Bool.True -> Illegal
        Bool.False -> Rational(1, n)
        Bool.Unknown -> throw IllegalStateException()
    }
}

fun gcd(a: Int, b: Int): Int {
    return gcdInner(abs(a), abs(b))
}

private fun gcdInner(a: Int, b: Int): Int {
    return if (b == 0) a else gcdInner(b, a % b)
}

fun rational(numerator: Int, denominator: Int): Expr<Nothing> {
    if (numerator < 0 && denominator < 0)
        return rational(-numerator, -denominator)
    if (numerator < 0 || denominator < 0)
        return rational(
            -abs(numerator),
            abs(denominator),
        )
    if (denominator == 0) return Illegal
    if (denominator == 1) return integer(numerator)
    val g = gcd(numerator, denominator)
    if (g == 1) return Rational(numerator, denominator)
    return rational(numerator / g, denominator / g)
}

data class Rational(val numerator: Int, val denominator: Int) : Integral {
    override val sign: Sign = when {
        numerator == 0 -> Sign.Zero
        numerator > 0 -> Sign.Positive
        else -> Sign.Negative
    }

    override fun inverse(): Expr<Nothing> = when (isZero()) {
        Bool.True -> Illegal
        Bool.False -> Rational(numerator, denominator)
        Bool.Unknown -> throw IllegalStateException()
    }

    override fun toRational() = this
}
