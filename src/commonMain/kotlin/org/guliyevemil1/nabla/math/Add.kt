@file:Suppress("UNCHECKED_CAST")

package org.guliyevemil1.nabla.math

class Add<T>(s: List<Expr<T>>) : Expr<T> {
    constructor(vararg s: Expr<T>) : this(s.asList())

    val summands: List<Expr<T>> by lazy {
        s.flatMap {
            if (it is Add) {
                it.summands.sortedWith(ExprComparator)
            } else {
                listOf(it)
            }
        }
    }

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
        return scale(integer(2), l) as Expr<T>
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
