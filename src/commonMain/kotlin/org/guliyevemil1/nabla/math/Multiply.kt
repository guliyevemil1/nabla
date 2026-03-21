package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.groupWith

data class Scale(val factor: Constant, val expr: Expr<Any?>) : Expr<Any?> {
    override val isConstant: Boolean = expr.isConstant

    override fun render(): String =
        if (factor == integer(-1)) {
            """-${expr.render()}"""
        } else if (expr.isSimple) {
            """${factor.render()} ${expr.render()}"""
        } else {
            """${factor.render()} \left(${expr.render()}\right)"""
        }

    override fun toLisp(): String = buildString {
        append("(scale ${factor.toLisp()} ${expr.toLisp()})")
    }

}

fun <T> List<Expr<T>>.foldMultiply(): Expr<T> = fold<Expr<T>, Expr<T>>(initial = One, ::multiplyBinary)

class Multiply<T>(m: List<Expr<T>>) : Expr<T> {
    constructor(vararg m: Expr<T>) : this(m.asList())

    override fun equals(other: Any?): Boolean = other is Multiply<*> &&
            multiplicants.size == other.multiplicants.size &&
            multiplicants.zip(other.multiplicants).all { (a, b) -> a == b }

    override fun hashCode(): Int = multiplicants.fold(0) { acc, x -> acc + 31 * x.hashCode() }

    val multiplicants: List<Expr<T>> = m
        .flatMap {
            if (it is Multiply) {
                it.multiplicants
            } else {
                listOf(it)
            }
        }
        .sortedWith(ExprComparator)
        .groupWith(::equalBases)
        .map { it.foldMultiply() }

    override val isSimple: Boolean = multiplicants.all { it.isSimple }
    override val isConstant: Boolean = multiplicants.all { it.isConstant }

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

    override fun toLisp(): String = buildString {
        append("(* ")
        multiplicants.joinTo(this, separator = " ") { it.toLisp() }
        append(")")
    }
}

fun <T> negate(m: Expr<T>): Expr<T> = multiply(NegOne, m)

fun <T> scale(factor: Expr<Nothing>, expr: Expr<T>): Expr<T> =
    when {
        factor == Bottom -> Bottom
        factor == Zero -> Zero
        factor == One -> expr

        expr is Add -> expr.map { scale(factor, it) }

        factor is Integral && expr is Integral -> {
            multiply(factor, expr)
        }

        factor is Integral && expr is Scale -> {
            scale(multiply(factor, expr.factor), expr.expr) as Expr<T>
        }

        else -> Scale(factor as Constant, expr) as Expr<T>
    }

fun multiply(l: Integral, r: Integral): Expr<Nothing> {
    if (l is Integer && r is Integer) return integer(l.n * r.n)
    val ratL = l.toRational()
    val ratR = r.toRational()
    return rational(
        numerator = ratL.numerator * ratR.numerator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun <T> multiply(vararg m: Expr<T>): Expr<T> = multiply(m.asList())

fun <T> multiply(m: List<Expr<T>>): Expr<T> =
    Multiply(m).let {
        when (it.multiplicants.size) {
            0 -> One
            1 -> it.multiplicants[0]
            else -> it.multiplicants.foldMultiply()
        }
    }

fun <T> multiplyBinary(l: Expr<T>, r: Expr<T>): Expr<T> {
    return when {
        l == Zero || r == Zero -> Zero
        l == One -> r
        r == One -> l
        l is Bottom || r is Bottom -> Bottom
        l is Integral && r is Integral -> multiply(l, r)

        l is Integral && r is Scale -> scale(multiply(l, r.factor), r.expr) as Expr<T>
        l is Scale && r is Integral -> scale(multiply(l.factor, r), l.expr) as Expr<T>

        l is Integral && r is Multiply -> scale(l, r)
        l is Multiply && r is Integral -> scale(r, l)

        l is Integral && r is Divide -> Divide(multiply(l, r.numerator), r.denominator)
        l is Divide && r is Integral -> Divide(multiply(l.numerator, r), l.denominator)

        l is Integral -> scale(l, r)
        r is Integral -> scale(r, l)

        l is XPow && r is XPow -> xPow(add(l.pow, r.pow)) as Expr<T>

        l == r -> pow(l, integer(2))
        l is Pow && r is Pow && l.base == r.base -> pow(l.base, add(l.pow, r.pow))
        l is Pow && l.base == r -> pow(l, add(l.pow, One))
        r is Pow && l == r.base -> pow(r, add(r.pow, One))

        l is Add && r is Add -> add(l.summands.flatMap { ls ->
            r.summands.map { multiply(ls, it) }
        })

        l is Add -> l.map { multiply(it, r) }
        r is Add -> r.map { multiply(l, it) }

        l is Scale && r is Scale -> scale(
            multiply(l.factor, r.factor),
            multiply(l.expr, r.expr),
        ) as Expr<T>

        l is Scale -> Scale(
            factor = l.factor,
            expr = multiply(l.expr, r),
        ) as Expr<T>

        r is Scale -> Scale(
            factor = r.factor,
            expr = multiply(l, r.expr),
        ) as Expr<T>

        l is Multiply && r is Multiply -> multiply(l.multiplicants + r.multiplicants)
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
