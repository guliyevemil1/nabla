@file:Suppress("UNCHECKED_CAST")

package org.guliyevemil1.nabla.math

import org.guliyevemil1.nabla.groupWith

fun <T> List<Expr<T>>.foldAdd(): Expr<T> = fold<Expr<T>, Expr<T>>(initial = Zero, ::addBinary)

class Add<T>(s: List<Expr<T>>) : Expr<T> {
    constructor(vararg s: Expr<T>) : this(s.asList())

    override fun equals(other: Any?): Boolean = other is Add<*> &&
            summands.size == other.summands.size &&
            summands.zip(other.summands).all { (a, b) -> a == b }

    override fun hashCode(): Int = summands.fold(0) { acc, x -> acc + 31 * x.hashCode() }
    val summands: List<Expr<T>> =
        s.flatMap {
            if (it is Add) {
                it.summands
            } else {
                listOf(it)
            }
        }.sortedWith(ExprComparator)
            .groupWith(::equalsUpToConstant)
            .map(List<Expr<T>>::foldAdd)

    override val isConstant: Boolean = summands.all { it.isConstant }

    fun <U> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        add(summands.map(f))

    override fun render(): String = buildString {
        if (summands.size == 1) {
            summands[0].render()
        } else {
            summands.forEachIndexed { index, m ->
                val r = m.render()
                if (index > 0 && !r.startsWith("-")) {
                    append("+")
                }
                append(r)
            }
        }
    }

    override fun toLisp(): String = buildString {
        append("(+ ")
        summands.joinTo(this, separator = " ") { it.toLisp() }
        append(")")
    }
}

fun add(l: Integral, r: Integral): Expr<Nothing> {
    if (l is Integer && r is Integer) return integer(l.n + r.n)
    val ratL = l.toRational()
    val ratR = r.toRational()
    return rational(
        numerator = ratL.numerator * ratR.denominator + ratR.numerator * ratL.denominator,
        denominator = ratL.denominator * ratR.denominator,
    )
}

fun <T> add(vararg s: Expr<T>): Expr<T> = add(s.asList())

fun <T> add(s: List<Expr<T>>): Expr<T> {
    return Add(s).let {
        when (it.summands.size) {
            0 -> Zero
            1 -> it.summands[0]
            else -> it.summands.foldAdd()
        }
    }
}

private fun <T> addBinary(l: Expr<T>, r: Expr<T>): Expr<T> =
    when {
        l == Zero -> r
        r == Zero -> l
        l is Bottom || r is Bottom -> Bottom
        l == r -> scale(integer(2), l)

        l is Integral && r is Integral -> add(l, r)

        l is Scale && r is Scale && l.expr == r.expr ->
            scale(
                add(l.factor, r.factor),
                add(l.expr, r.expr),
            ) as Expr<T>

        l is Scale && l.expr == r ->
            scale(
                add(l.factor, One),
                r,
            )

        r is Scale && l == r.expr ->
            scale(
                add(r.factor, One),
                l,
            )

        l is Add && r is Add -> add(l.summands + r.summands)
        l is Add -> Add(l.summands + r)
        r is Add -> Add(listOf(l) + r.summands)
        else -> Add(l, r)
    }
