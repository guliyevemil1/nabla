package org.guliyevemil1.nabla.math

import kotlin.math.sign

val Zero = integer(0)
val One = integer(1)
val NegOne = integer(-1)
val OneHalf = Rational(1, 2)

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

val Expr<Nothing>.sign: Sign
    get() = when (this) {
        is Integer -> when {
            n < 0 -> Sign.Negative
            n == 0 -> Sign.Zero
            else -> Sign.Positive
        }

        is Rational -> {
            if (numerator == 0) return Sign.Zero
            if (denominator == 0) return Sign.Unknown
            val m = numerator.sign * denominator.sign
            if (m > 0) Sign.Positive
            else Sign.Negative
        }

        is Pow<Nothing> -> this.base.sign

        is Multiply<Nothing> -> this.multiplicants
            .map { it.sign }
            .fold(Sign.Positive, Sign::times)

        is Divide<Nothing> -> this.numerator.sign * this.denominator.sign

        is Exp<Nothing> -> Sign.Positive

        is Add<Nothing> -> TODO()
        is Log<Nothing> -> TODO()

        is Scale -> Sign.Unknown
        is Invert -> Sign.Unknown
        is Differentiate -> Sign.Unknown
        is Integrate -> Sign.Unknown
        Bottom -> Sign.Unknown
        CosX -> Sign.Unknown
        SinX -> Sign.Unknown
        is XPow -> Sign.Unknown
    }

val Expr<Nothing>.isPositive: Bool
    get() = when (sign) {
        Sign.Positive -> Bool.True
        Sign.Unknown -> Bool.Unknown
        else -> Bool.False
    }

val Expr<Nothing>.isZero: Bool
    get() = when (sign) {
        Sign.Zero -> Bool.True
        Sign.Unknown -> Bool.Unknown
        else -> Bool.False
    }

val Expr<Nothing>.isNegative: Bool
    get() = when (sign) {
        Sign.Negative -> Bool.True
        Sign.Unknown -> Bool.Unknown
        else -> Bool.False
    }

val Expr<Nothing>.isNonPositive: Bool get() = !isPositive
val Expr<Nothing>.isNonNegative: Bool get() = !isNegative

sealed interface Constant : Expr<Nothing> {
    override val isSimple: Boolean
        get() = true

    override val isConstant: Boolean
        get() = true

    fun inverse(): Expr<Nothing> = when (isZero) {
        Bool.True -> Bottom
        Bool.False -> Divide(One, this)
        else -> TODO()
    }
}

enum class Sign {
    Zero,
    Positive,
    Negative,
    Unknown;

    operator fun times(b: Sign): Sign {
        if (this == Unknown || b == Unknown) return Unknown
        if (this == Zero || b == Zero) return Zero
        if (this == b) return Positive
        return Negative
    }
}
