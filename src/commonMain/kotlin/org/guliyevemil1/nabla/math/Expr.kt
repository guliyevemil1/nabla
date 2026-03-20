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
    Sqrt::class,
    Differentiate::class,
    Integrate::class,
    Add::class,
    Multiply::class,
    Divide::class,
    Invert::class,
).mapIndexed { index, klass -> klass to index }.toMap()

private fun compareExpr(a: Expr<*>, b: Expr<*>): Int =
    when {
        a is Scale && b is Scale -> compareExpr(a.expr, b.expr)
        a is Scale -> compareExpr(a.expr, b)
        b is Scale -> compareExpr(a, b.expr)
        a is Integral && b is Integral -> {
            val ar = a.toRational()
            val br = a.toRational()
            compareValues(ar.numerator * br.denominator, br.numerator * ar.denominator)
        }

        a is Integral -> -1
        b is Integral -> 1
        else -> compareValuesBy(a, b) { ExprOrdering[it::class] }
    }

val ExprComparator: Comparator<Expr<*>> = Comparator { a, b -> compareExpr(a, b) }

sealed interface Expr<out T> {
    val isConstant: Boolean

    val asConstant: Expr<Nothing>?
        get() = if (isConstant) {
            this as Expr<Nothing>
        } else null

    fun render(): String

    fun toLisp(): String

    val isSimple: Boolean
        get() = false
}

object Bottom : Expr<Nothing> {
    override val isConstant: Boolean = true
    override fun render(): String = """\bot"""
    override fun toLisp(): String = "bottom"
}
