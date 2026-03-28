package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.util.groupWith
import org.guliyevemil1.nabla.util.splitBy
import kotlin.let

fun <T> List<Expr<T>>.flattenMultiply(): List<Expr<T>> = flatMap {
    if (it is Multiply) {
        it.multiplicants
    } else {
        listOf(it)
    }
}

fun <T> List<Expr<T>>.foldMultiply(): Expr<T> =
    if (isEmpty()) One
    else if (size == 1) first()
    else reduce<Expr<T>, Expr<T>>(::multiplyBinary)

class Multiply<T>(val multiplicants: List<Expr<T>>, unit: Unit = Unit) : Expr<T> {

    companion object {
        fun <T> render(m: Expr<T>): String =
            if (m !is Multiply) {
                m.render()
            } else {
                buildString {
                    if (m.multiplicants.size == 1) {
                        m.multiplicants[0].render()
                    } else {
                        m.multiplicants.forEach { m ->
                            if (!m.isSimple) append("""\left(""")
                            append(m.render().trim())
                            if (!m.isSimple) append("""\right)""")
                        }
                    }
                }
            }
    }

    constructor(l: Expr<T>, r: Expr<T>) : this(multiplicants = listOf(l, r))

    constructor(m: List<Expr<T>>) : this(
        multiplicants = m
            .flattenMultiply()
            .sortedWith(ExprComparator)
            .groupWith(::equalBases)
            .map {
                if (it.size == 1) {
                    it.first()
                } else {
                    it.foldMultiply()
                }
            }
    )

    override fun equals(other: Any?): Boolean = other is Multiply<*> &&
            multiplicants.size == other.multiplicants.size &&
            multiplicants.zip(other.multiplicants).all { (a, b) -> a == b }

    override fun hashCode(): Int = multiplicants.fold(0) { acc, x -> acc + 31 * x.hashCode() }

    override val isSimple: Boolean = multiplicants.all { it.isSimple }
    override fun matches(other: Expr<*>): Boolean {
        TODO("Not yet implemented")
    }

    override val isConstant: Boolean = multiplicants.all { it.isConstant }

    fun <U> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        multiply(multiplicants.map(f))

    override fun render(): String {
        val (n, d) = multiplicants
            .splitBy {
                when (it) {
                    is Pow if it.pow.isNegative == Bool.True -> false
                    is Exp if it.pow is Scale && it.pow.factor.isNegative == Bool.True -> false
                    else -> true
                }
            }
        val nn = multiply(n)
        val dd = multiply(d.map { pow(it, integer(-1)) })
        return if (dd == One) render(nn) else """\frac{${render(nn)}}{${render(dd)}}"""
    }

    override fun toLisp(): String = buildString {
        append("(* ")
        multiplicants.joinTo(this, separator = " ") { it.toLisp() }
        append(")")
    }
}

fun <T> negate(m: Expr<T>): Expr<T> = multiply(NegOne, m)

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

        l is Integral && r is Scale -> scale(multiply(l, r.factor), r.expr)
        l is Scale && r is Integral -> scale(multiply(l.factor, r), l.expr)

        l is Integral && r is Multiply -> scale(l, r)
        l is Multiply && r is Integral -> scale(r, l)

        l is Integral && r is Divide -> divide(multiply(l, r.numerator), r.denominator)
        l is Divide && r is Integral -> divide(multiply(l.numerator, r), l.denominator)

        l is Integral -> scale(l, r)
        r is Integral -> scale(r, l)

        l is XPow && r is XPow -> xPow(add(l.pow, r.pow)) as Expr<T>

        l == r -> pow(l, integer(2))
        l is Pow && r is Pow && l.base == r.base -> pow(l.base, add(l.pow, r.pow))
        l is Pow && l.base is XPow && r is XPow -> xPow(add(l.pow, r.pow)) as Expr<T>
        r is Pow && r.base is XPow && l is XPow -> xPow(add(l.pow, r.pow)) as Expr<T>
        l is Pow && l.base == r -> pow(l.base, add(l.pow, One))
        r is Pow && l == r.base -> pow(r.base, add(r.pow, One))

        l is Add && r is Add -> add(l.summands.flatMap { ls ->
            r.summands.map { multiply(ls, it) }
        })

        l is Add -> l.map { multiply(it, r) }
        r is Add -> r.map { multiply(l, it) }

        l is Scale && r is Scale -> scale(
            multiply(l.factor, r.factor),
            multiply(l.expr, r.expr),
        )

        l is Scale -> scale(
            factor = l.factor,
            expr = multiply(l.expr, r),
        )

        r is Scale -> scale(
            factor = r.factor,
            expr = multiply(l, r.expr),
        )

        l is Exp && r is Exp -> e(add(l.pow, r.pow))

        l is Multiply && r is Multiply -> multiply(l.multiplicants + r.multiplicants)
        l is Multiply -> Multiply(l.multiplicants + r)
        r is Multiply -> Multiply(listOf(l) + r.multiplicants)

        l is Divide && r is Divide ->
            divide(
                multiply(l.numerator, r.numerator),
                multiply(l.denominator, r.denominator)
            )

        l is Divide -> divide(
            multiply(l.numerator, r),
            l.denominator,
        )

        r is Divide -> {
            if (r.numerator == One) divide(l, r)
            divide(
                multiply(l, r.numerator),
                r.denominator,
            )
        }

        else -> Multiply(l, r)
    }
}
