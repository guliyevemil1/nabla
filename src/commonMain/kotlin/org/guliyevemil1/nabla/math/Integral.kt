package org.guliyevemil1.nabla.math

import kotlin.math.abs

sealed interface Integral : Constant {
    fun toRational(): Rational

    override fun inverse(): Expr<Nothing>
}

private val integerMap = HashMap<Int, Integer>()

fun integer(n: Int): Integer =
    integerMap[n] ?: Integer(n).also { integerMap[n] = it }

data class Integer(val n: Int) : Integral {
    override fun toRational() = Rational(n, 1)

    override fun inverse(): Expr<Nothing> = when (isZero) {
        Bool.True -> Bottom
        Bool.False -> Rational(1, n)
        Bool.Unknown -> throw IllegalStateException()
    }

    override fun render(): String = """$n"""
    override fun toLisp(): String = "$n"
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
    if (denominator < 0)
        return rational(
            -numerator,
            -denominator,
        )
    if (denominator == 0) return Bottom
    if (denominator == 1) return integer(numerator)
    val g = gcd(numerator, denominator)
    if (g == 1) return Rational(numerator, denominator)
    return rational(numerator / g, denominator / g)
}

data class Rational(val numerator: Int, val denominator: Int) : Integral {

    override fun inverse(): Expr<Nothing> = when (isZero) {
        Bool.True -> Bottom
        Bool.False -> Rational(numerator, denominator)
        Bool.Unknown -> throw IllegalStateException()
    }

    override fun toRational() = this
    override fun render(): String = """\frac{$numerator}{$denominator}"""
    override fun toLisp(): String = "(/ $numerator $denominator)"
}
