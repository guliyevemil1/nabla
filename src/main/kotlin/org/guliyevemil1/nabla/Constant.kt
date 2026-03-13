package org.guliyevemil1.nabla

import org.guliyevemil1.nabla.ConstExpr.Companion.constExpr
import kotlin.math.abs

val Zero = integer(0)
val One = integer(1)
val NegOne = integer(-1)

enum class Bool {
    True,
    False,
    Unknown;

    companion object {
        fun Boolean.toBool() = when (this) {
            true -> True
            false -> False
        }
    }

    operator fun not() = when (this) {
        True -> False
        False -> True
        Unknown -> Unknown
    }

    infix fun and(that: Bool) =
        if (this == True && that == True) True
        else if (this == Unknown || that == Unknown) Unknown
        else False

}

sealed interface Constant : Base {
    val sign: Sign

    fun isPositive(): Bool = when (sign) {
        Sign.Positive -> Bool.True
        Sign.Unknown -> Bool.Unknown
        else -> Bool.False
    }

    fun isZero(): Bool = when (sign) {
        Sign.Zero -> Bool.True
        Sign.Unknown -> Bool.Unknown
        else -> Bool.False
    }

    fun isNegative(): Bool = when (sign) {
        Sign.Negative -> Bool.True
        Sign.Unknown -> Bool.Unknown
        else -> Bool.False
    }

    fun isNonPositive(): Bool = !isPositive()
    fun isNonNegative(): Bool = !isNegative()

    fun toRational(): Rational?
    fun inverse(): Constant = when (isZero()) {
        Bool.True -> Illegal
        Bool.False -> constExpr(Divide(One, this))
        else -> TODO()
    }
}

enum class Sign {
    Zero,
    Positive,
    Negative,
    Unknown,
}

object Illegal : Constant {
    override val sign: Sign = Sign.Unknown
    override fun toRational(): Rational? = null
    override fun inverse() = Illegal
}

private val integerMap = HashMap<Int, Integer>()

fun integer(n: Int): Integer = integerMap.computeIfAbsent(n, ::Integer)

data class Integer(val n: Int) : Constant {
    override val sign: Sign = when {
        n < 0 -> Sign.Negative
        n == 0 -> Sign.Zero
        else -> Sign.Positive
    }

    override fun toRational() = Rational(n, 1)

    override fun inverse(): Constant = when (isZero()) {
        Bool.True -> Illegal
        Bool.False -> Rational(1, n)
        else -> TODO()
    }
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
    if (denominator == 1) return integer(numerator)
    val g = gcd(numerator, denominator)
    if (g == 1) return Rational(numerator, denominator)
    return rational(numerator / g, denominator / g)
}

data class Rational(val numerator: Int, val denominator: Int) : Constant {
    override val sign: Sign = when {
        numerator == 0 -> Sign.Zero
        numerator > 0 -> Sign.Positive
        else -> Sign.Negative
    }

    override fun inverse(): Constant = when (isZero()) {
        Bool.True -> Illegal
        Bool.False -> Rational(numerator, denominator)
        else -> TODO()
    }

    override fun toRational() = this
}

data class ConstExpr(val b: Base) : Constant {
    companion object {
        fun constExpr(b: Base): Constant =
            when (b) {
                X -> Illegal
                CosX -> Illegal
                ExpX -> Illegal
                SinX -> Illegal

                Illegal -> Illegal
                is Constant -> b

                is Differentiate -> Illegal
                is Integrate -> Illegal

                is Invert -> TODO()

                is Log ->
                    ConstExpr(Log(constExpr(b.base)))

                is Sqrt ->
                    ConstExpr(Sqrt(constExpr(b.base)))

                is Add -> TODO()
                is Multiply -> TODO()
                is Divide -> TODO()
                is Pow -> TODO()
            }
    }

    override val sign: Sign = Sign.Unknown

    override fun toRational(): Rational? = null
}
