@file:Suppress("UNCHECKED_CAST")

package org.guliyevemil1.nabla

fun <T : Base> add(vararg summands: Expr<T>): Expr<T> = add(summands.asList())

fun <T : Base> add(summands: List<Expr<T>>): Expr<T> {
    return when (summands.size) {
        0 -> Illegal
        1 -> summands[0]
        else -> summands
            .reduce { l, r ->
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

fun <T : Base> multiply(vararg multiplicants: Expr<T>): Expr<T> = multiply(multiplicants.asList())

fun <T : Base> multiply(multiplicants: List<Expr<T>>): Expr<T> {
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

fun <T : Base> divide(l: Expr<T>, r: Expr<T>): Expr<T> {
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integral && r is Integral) {
        if (l is Integer && r is Integer) return rational(l.n, r.n)
        val ratL = l.toRational()
        val ratR = l.toRational()
        if (ratL == null || ratR == null) {
            return Illegal
        }
        return rational(
            numerator = ratL.numerator * ratR.denominator,
            denominator = ratL.denominator * ratR.numerator,
        )
    }
    TODO()
}

private val powMap = HashMap<Int, Pow<Base>>()

fun <T : Base> pow(base: Expr<T>, n: Int): Expr<T> {
    if (base is X) {
        return powMap.computeIfAbsent(n) { Pow(X, n) } as Expr<T>
    }
    if (n < 0) return Illegal
    if (n == 0) return One
    return multiply(base, pow(base, n - 1))
}
