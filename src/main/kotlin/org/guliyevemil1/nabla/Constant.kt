package org.guliyevemil1.nabla

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

sealed interface Constant : Base, Expr<Nothing> {
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

    fun inverse(): Expr<Constant> = when (isZero()) {
        Bool.True -> Illegal
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
