@file:Suppress("UNCHECKED_CAST")

package org.guliyevemil1.nabla.math

fun <T> add(vararg summands: Expr<T>): Expr<T> = add(summands.asList())

fun <T> add(summands: List<Expr<T>>): Expr<T> {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands.reduce(::add)
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
    if (l == r) {
        return Scale(integer(2), l) as Expr<T>
    }
    return when {
        l is Scale && r is Scale && l.expr == r.expr ->
            Scale(
                add(l.factor, r.factor),
                add(l.expr, r.expr),
            ) as Expr<T>

        l is Scale && l.expr == r ->
            Scale(
                add(l.factor, One),
                r,
            ) as Expr<T>

        r is Scale && l == r.expr ->
            Scale(
                add(r.factor, One),
                l,
            ) as Expr<T>

        l is Add && r is Add -> add(l.summands + r.summands)
        l is Add -> add(l.summands + r)
        r is Add -> add(r.summands + l)
        else -> Add(l, r)
    }
}

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
        l is Integral && r is Scale -> multiply(multiply(l, r.factor), r.expr) as Expr<T>
        l is Scale && r is Integral -> multiply(multiply(l.factor, r), l.expr) as Expr<T>

        l is Integral -> Scale(l, r) as Expr<T>
        r is Integral -> Scale(r, l) as Expr<T>

        l is XPow && r is XPow -> xPow(add(l.pow, r.pow)) as Expr<T>

        l is Add && r is Add -> add(l.summands.flatMap { ls ->
            r.summands.map { rs ->
                multiply(ls, rs)
            }
        })

        l is Add -> add(l.summands.map { ls ->
            multiply(ls, r)
        })

        r is Add -> add(r.summands.map { rs ->
            multiply(l, rs)
        })

        l is Scale && r is Scale -> Scale(
            factor = multiply(l.factor, r.factor),
            expr = multiply(l.expr, r.expr),
        ) as Expr<T>

        l is Scale -> Scale(
            factor = l.factor,
            expr = multiply(l.expr, r),
        ) as Expr<T>

        r is Scale -> Scale(
            factor = r.factor,
            expr = multiply(l, r.expr),
        ) as Expr<T>

        l is Multiply && r is Multiply -> Multiply(l.multiplicants + r.multiplicants)
        l is Multiply -> Multiply(l.multiplicants + r)
        r is Multiply -> Multiply(listOf(l) + r.multiplicants)

        l is Divide && r is Divide -> divide(
            multiply(l.numerator, r.numerator),
            multiply(l.denominator, r.denominator)
        )

        l is Divide -> divide(
            multiply(l.numerator, r),
            l.denominator,
        )

        r is Divide -> divide(
            multiply(l, r.numerator),
            r.denominator,
        )

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
        val ratL = l.toRational() ?: return Illegal
        val ratR = l.toRational() ?: return Illegal
        return rational(
            numerator = ratL.numerator * ratR.denominator,
            denominator = ratL.denominator * ratR.numerator,
        )
    }
    return when {
        l is Scale && r is Scale -> Scale(
            factor = divide(l.factor, r.factor),
            expr = divide(l.expr, r.expr),
        ) as Expr<T>

        l == r -> One
        else -> multiply(l, Divide(One, r))
    }
}

fun <T> pow(base: Expr<T>, n: Int): Expr<T> {
    if (base is XPow) {
        return xPow(multiply(base.pow, integer(n))) as Expr<T>
    }
    if (n < 0) return Illegal
    if (n == 0) return One
    return multiply(base, pow(base, n - 1))
}
