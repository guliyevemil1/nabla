@file:Suppress("UNCHECKED_CAST")

package org.guliyevemil1.nabla.math

class Add<T>(s: List<Expr<T>>) : Expr<T> {
    constructor(vararg s: Expr<T>) : this(s.asList())

    val summands: List<Expr<T>> =
        s.flatMap {
            if (it is Add) {
                it.summands
            } else {
                listOf(it)
            }
        }.sortedWith(ExprComparator)

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

fun <T> add(vararg summands: Expr<T>): Expr<T> = add(summands.asList())

fun <T> add(summands: List<Expr<T>>): Expr<T> {
    return when (summands.size) {
        0 -> Zero
        1 -> summands[0]
        else -> summands.sortedWith(ExprComparator).reduce(::add)
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

fun <T> add(l: Expr<T>, r: Expr<T>): Expr<T> =
    when {
        l == Zero -> r
        r == Zero -> l
        l is Bottom || r is Bottom -> Bottom
        l is Integral && r is Integral -> add(l, r)

        l == r -> scale(integer(2), l)
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

        l is Add && r is Add -> Add(l.summands + r.summands)
        l is Add -> Add(l.summands + r)
        r is Add -> Add(listOf(l) + r.summands)
        else -> Add(l, r)
    }
