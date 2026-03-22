package org.guliyevemil1.nabla.math

fun isqrt(n: Int): Int? {
    if (n < 0) throw IllegalArgumentException("Cannot compute square root of negative number: $n")
    if (n < 2) return n

    var x = n
    var y = (x + 1) / 2

    while (y < x) {
        x = y
        y = (x + n / x) / 2
    }

    if (x * x == n) return x
    return null
}

fun <T> sqrt(c: Expr<T>): Expr<T> =
    when (c) {
        Zero -> Zero
        One -> One
        is Integer -> {
            val r = isqrt(c.n) ?: return pow(c, OneHalf)
            return integer(r)
        }

        is Rational -> {
            val n = isqrt(c.numerator)
            val d = isqrt(c.denominator)
            if (n == null && d == null) return pow(c, OneHalf)
            else if (n != null && d != null) return rational(n, d)
            val ni = integer(c.numerator)
            val di = integer(c.denominator)
            if (n == null && d != null) return divide(pow(ni, OneHalf), di)
            if (n != null && d == null) return divide(
                multiply(ni, pow(di, OneHalf)),
                di
            )
            throw IllegalStateException("Cannot compute square root of $c")
        }

        is Constant -> pow(c, OneHalf)
        is XPow -> xPow(divide(c.pow, integer(2))) as Expr<T>
        is Multiply if c.multiplicants.size == 2 && c.multiplicants[0] == c.multiplicants[1] -> {
            c.multiplicants[0]
        }

        is Scale -> multiply(sqrt(c.factor), sqrt(c.expr)) as Expr<T>
        is Multiply -> multiply(c.multiplicants.map { sqrt(it) })
        is Divide -> divide(sqrt(c.numerator), sqrt(c.denominator))

        else -> pow(c, OneHalf)
    }
