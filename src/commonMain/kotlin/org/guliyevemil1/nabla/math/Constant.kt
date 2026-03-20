package org.guliyevemil1.nabla.math

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

sealed interface Constant : Expr<Nothing> {
    override val isSimple: Boolean
        get() = true

    override val isConstant: Boolean
        get() = true
    val sign: Sign

    val isPositive: Bool
        get() = when (sign) {
            Sign.Positive -> Bool.True
            Sign.Unknown -> Bool.Unknown
            else -> Bool.False
        }

    val isZero: Bool
        get() = when (sign) {
            Sign.Zero -> Bool.True
            Sign.Unknown -> Bool.Unknown
            else -> Bool.False
        }

    val isNegative: Bool
        get() = when (sign) {
            Sign.Negative -> Bool.True
            Sign.Unknown -> Bool.Unknown
            else -> Bool.False
        }

    val isNonPositive: Bool get() = !isPositive
    val isNonNegative: Bool get() = !isNegative

    fun inverse(): Expr<Constant> = when (isZero) {
        Bool.True -> Bottom
        Bool.False -> Divide(One, this)
        else -> TODO()
    }
}

enum class Sign {
    Zero,
    Positive,
    Negative,
    Unknown,
}
