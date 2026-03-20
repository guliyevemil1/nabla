package org.guliyevemil1.nabla.math

import kotlin.reflect.KClass

val ExprOrdering: Map<KClass<out Expr<*>>, Int> = listOf(
    Integer::class,
    Rational::class,
    XPow::class,
    Scale::class,
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

val ExprComparator: Comparator<Expr<*>> = compareBy {
    ExprOrdering[it::class]
}

sealed interface Expr<out T> {
    fun render(): String

    fun toLisp(): String

    val isSimple: Boolean
        get() = false
}

object Illegal : Expr<Nothing> {
    override fun render(): String = """\bot"""
    override fun toLisp(): String = "bottom"
}
