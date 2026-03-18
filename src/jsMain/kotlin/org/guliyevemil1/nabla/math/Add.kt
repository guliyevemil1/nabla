@file:Suppress("UNCHECKED_CAST")

package org.guliyevemil1.nabla.math

fun <T> add(vararg summands: Expr<T>): Expr<T> = add(summands.asList())

fun <T> add(summands: List<Expr<T>>): Expr<T> {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands
            .reduce(::add)
    }
}

fun <T> add(l: Expr<T>, r: Expr<T>): Expr<T> {
    if (l == Zero) return r
    if (r == Zero) return l
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integral && r is Integral) {
        if (l is Integer && r is Integer) return integer(l.n + r.n)
        val ratL = l.toRational() ?: return Add(l, r)
        val ratR = r.toRational() ?: return Add(l, r)
        return rational(
            numerator = ratL.numerator * ratR.denominator + ratR.numerator * ratL.denominator,
            denominator = ratL.denominator * ratR.denominator,
        )
    }
    return when {
        l is Add && r is Add -> add(l.summands + r.summands)
        l is Add -> add(l.summands + r)
        r is Add -> add(r.summands + l)
        else -> Add(l, r)
    }
}

fun <T> multiply(vararg multiplicants: Expr<T>): Expr<T> = multiply(multiplicants.asList())

fun <T> multiply(multiplicants: List<Expr<T>>): Expr<T> {
    return when (multiplicants.size) {
        0 -> One
        1 -> multiplicants[0]
        else -> multiplicants.reduce(::multiply)
    }
}

fun <T> multiply(l: Expr<T>, r: Expr<T>): Expr<T> {
    if (l == Zero || r == Zero) return Zero
    if (l == One) return r
    if (r == One) return l
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integral && r is Integral) {
        if (l is Integer && r is Integer) return integer(l.n * r.n)
        val ratL = l.toRational() ?: return Illegal
        val ratR = l.toRational() ?: return Illegal
        return rational(
            numerator = ratL.numerator * ratR.numerator,
            denominator = ratL.denominator * ratR.denominator,
        )
    }
    return when {
        l is Multiply && r is Multiply -> multiply(l.multiplicants + r.multiplicants)
        l is Multiply -> multiply(l.multiplicants + r)
        r is Multiply -> multiply(r.multiplicants + l)
        else -> Multiply(l, r)
    }
}

fun <T> divide(l: Expr<T>, r: Expr<T>): Expr<T> {
    if (r == Zero) return Illegal
    if (l == Zero) return Zero
    if (r == One) return l
    if (l is Illegal || r is Illegal) return Illegal
    if (l is Integral && r is Integral) {
        if (l is Integer && r is Integer) return rational(l.n, r.n)
        val ratL = l.toRational() ?: return Divide(l, r)
        val ratR = l.toRational() ?: return Divide(l, r)
        return rational(
            numerator = ratL.numerator * ratR.denominator,
            denominator = ratL.denominator * ratR.numerator,
        )
    }
    return when {
        l is Pow && r is X -> TODO()
        l is Multiply && r is Multiply -> TODO()
        l is Multiply -> TODO()
        r is Multiply -> TODO()
        l == r -> One
        else -> Divide(l, r)
    }
}

private val powMap = HashMap<Int, Pow<Any?>>()

fun <T> pow(base: Expr<T>, n: Int): Expr<T> {
    if (base is X) {
        return if (!powMap.containsKey(n)) {
            Pow(X, n).also {
                powMap[n] = it
            }
        } else {
            powMap[n]
        } as Expr<T>
    }
    if (n < 0) return Illegal
    if (n == 0) return One
    return multiply(base, pow(base, n - 1))
}
