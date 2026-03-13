package org.guliyevemil1.nabla

fun <T> add(vararg summands: Expr<T>): Expr<Any?> = add(summands.asList())

fun add(summands: List<Expr<*>>): Expr<Any?> {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands.reduce { l, r ->
            if (l is Illegal || r is Illegal) return@reduce Illegal
            if (l is Integral && r is Integral) {
                if (l is Integer && r is Integer) return@reduce Integer(l.n + r.n)
                val ratL = l.toRational()
                val ratR = l.toRational()
                if (ratL == null || ratR == null) {
                    return@reduce Illegal
                }
                return@reduce rational(
                    numerator = ratL.numerator * ratR.denominator + ratR.numerator * ratL.denominator,
                    denominator = ratL.denominator * ratR.denominator,
                )
            }
            TODO()
        }
    }
}

fun <T> multiply(vararg multiplicants: Expr<T>): Expr<Any?> = multiply(multiplicants.asList())

fun multiply(multiplicants: List<Expr<*>>): Expr<Any?> {
    return when (multiplicants.size) {
        0 -> One
        1 -> multiplicants[0]
        else -> multiplicants.reduce { l, r ->
            if (l is Illegal || r is Illegal) return@reduce Illegal
            if (l is Integral && r is Integral) {
                if (l is Integer && r is Integer) return@reduce Integer(l.n * r.n)
                val ratL = l.toRational()
                val ratR = l.toRational()
                if (ratL == null || ratR == null) {
                    return@reduce Illegal
                }
                return@reduce rational(
                    numerator = ratL.numerator * ratR.numerator,
                    denominator = ratL.denominator * ratR.denominator,
                )
            }
            TODO()
        }
    }
}

fun <T> divide(l: Expr<T>, r: Expr<T>): Expr<T> {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integer && r is Integer) return rational(l.n, r.n)
    val ratL = l.toRational()
    val ratR = l.toRational()
    if (ratL == null || ratR == null) {
        TODO()
    }
    return rational(
        numerator = ratL.numerator * ratR.denominator,
        denominator = ratL.denominator * ratR.numerator,
    )
}

private val powMap = HashMap<Int, Pow>()

fun pow(base: Constant, n: Int): Constant {
    if (n < 0) return Illegal
    if (n == 0) return One
    return multiply(base, pow(base, n - 1))
}

fun pow(base: Base, n: Int): Pow {
    if (base is X) {
        return powMap.computeIfAbsent(n) { Pow(X, n) }
    }
    return Pow(base, n)
}
