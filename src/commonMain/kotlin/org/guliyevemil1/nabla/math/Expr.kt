package org.guliyevemil1.nabla.math

import kotlin.reflect.KClass

val ExprOrdering: Map<KClass<out Expr<*>>, Int> = listOf(
    Scale::class,
    XPow::class,
    ExpX::class,
    SinX::class,
    CosX::class,
    Log::class,
    Pow::class,
    Differentiate::class,
    Integrate::class,
    Add::class,
    Multiply::class,
    Divide::class,
    Invert::class,
).mapIndexed { index, klass -> klass to index }.toMap()

fun equalsUpToConstant(a: Expr<*>, b: Expr<*>): Boolean =
    when {
        a is Scale && b is Scale -> equalsUpToConstant(a.expr, b.expr)
        a is Scale -> equalsUpToConstant(a.expr, b)
        b is Scale -> equalsUpToConstant(a, b.expr)
        a == Zero && b == Zero -> true
        a == Zero || b == Zero -> false
        a.isConstant && b.isConstant -> true
        else -> a == b
    }

fun equalBases(a: Expr<*>, b: Expr<*>): Boolean =
    when {
        a is Scale && b is Scale -> equalBases(a.expr, b.expr)
        a is Scale -> equalBases(a.expr, b)
        b is Scale -> equalBases(a, b.expr)
        a.isConstant && b.isConstant -> true
        else -> a == b
    }

fun compareExpr(a: Expr<*>, b: Expr<*>): Int =
    when {
        a is Scale && b is Scale -> compareExpr(a.expr, b.expr)
        a is Scale -> compareExpr(a.expr, b)
        b is Scale -> compareExpr(a, b.expr)
        a is Integral && b is Integral -> {
            val ar = a.toRational()
            val br = b.toRational()
            compareValues(ar.numerator * br.denominator, br.numerator * ar.denominator)
        }

        a is Integral -> -1
        b is Integral -> 1
        else -> {
            val cmp = compareValuesBy(a, b) { ExprOrdering[it::class] }
            if (cmp != 0) return cmp
            if (a is XPow && b is XPow) {
                return compareExpr(a.pow, b.pow)
            }
            if (a is Multiply && b is Multiply) {
                if (a == b) return 0
                a.multiplicants
                    .zip(b.multiplicants)
                    .forEach { (a, b) -> compareExpr(a, b).also { if (it != 0) return it } }
            }
            return 0
        }
    }

val ExprComparator: Comparator<Expr<*>> = Comparator { a, b -> compareExpr(a, b) }

sealed interface Expr<out T> {
    val isConstant: Boolean

    val asConstant: Constant?
        get() = if (isConstant) {
            this as Constant
        } else null

    fun render(): String

    fun toLisp(): String

    val isSimple: Boolean
        get() = false
}

object Bottom : Expr<Nothing> {
    override val isConstant: Boolean = false
    override fun render(): String = """\bot"""
    override fun toLisp(): String = "bottom"
}
