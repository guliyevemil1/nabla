package org.guliyevemil1.nabla.math

class Scale(val factor: Expr<Nothing>, val expr: Expr<Any?>) : Expr<Any?> {
    override fun render(): String =
        if (factor == integer(-1)) {
            """-${expr.render()}"""
        } else {
            """${factor.render()} ${expr.render()}"""
        }
}

class Multiply<T>(m: List<Expr<T>>) : Expr<T> {
    constructor(vararg m: Expr<T>) : this(m.asList())

    val multiplicants: List<Expr<T>> by lazy {
        m.flatMap {
            if (it is Multiply) {
                it.multiplicants
            } else {
                listOf(it)
            }
        }.sortedWith(ExprComparator)
    }

    fun <U> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        multiply(multiplicants.map(f))

    override fun render(): String = buildString {
        if (multiplicants.size == 1) {
            multiplicants[0].render()
        } else {
            multiplicants.forEachIndexed { index, m ->
                if (!m.isSimple) append("""\left(""")
                append(m.render().trim())
                if (!m.isSimple) append("""\right)""")
            }
        }
    }
}

fun negate(m: Expr<Any?>): Expr<Any?> = multiply(NegOne, m)

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

        l is Integral && r is Multiply -> multiply(r.multiplicants + l)
        l is Multiply && r is Integral -> multiply(l.multiplicants + r)

        l is Integral && r is Divide -> Divide(multiply(l, r.numerator), r.denominator)
        l is Divide && r is Integral -> Divide(multiply(l.numerator, r), l.denominator)

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

        l is Divide && r is Divide ->
            divide(
                multiply(l.numerator, r.numerator),
                multiply(l.denominator, r.denominator)
            )

        l is Divide -> Divide(
            multiply(l.numerator, r),
            l.denominator,
        )

        r is Divide -> {
            if (r.numerator == One) Divide(l, r)
            divide(
                multiply(l, r.numerator),
                r.denominator,
            )
        }

        else -> Multiply(l, r)
    }
}
