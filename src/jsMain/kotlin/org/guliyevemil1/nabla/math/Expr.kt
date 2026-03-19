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

val ExprComparator: Comparator<Expr<*>> = compareBy { ExprOrdering[it::class] }

sealed interface Expr<out T> {
    fun render(): String

    val isSimple
        get() = when (this) {
            is Constant -> true
            is ExpX -> true
            is SinX -> true
            is CosX -> true
            is XPow -> true
            is Log -> true
            else -> false
        }
}

object Illegal : Expr<Nothing> {
    override fun render(): String = """\bot"""
}

class Add<T>(s: List<Expr<T>>) : Expr<T> {
    constructor(vararg s: Expr<T>) : this(s.asList())

    val summands: List<Expr<T>> by lazy {
        s.flatMap {
            if (it is Add) {
                it.summands
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
                if (index > 0) {
                    when (m) {
                        is Constant if m.isNegative == Bool.True ->
                            "-"

                        is Scale if m.factor is Constant && m.factor.isNegative == Bool.True ->
                            "-"

                        else ->
                            "+"
                    }.also { append(it) }
                }
                append(m.render())
            }
        }
    }
}

class Scale(val factor: Expr<Nothing>, val expr: Expr<Any?>) : Expr<Any?> {
    override fun render(): String =
        if (factor == integer(-1)) {
            """-${expr.render()}"""
        } else if (factor.isSimple && expr.isSimple) {
            """${factor.render()} ${expr.render()}"""
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
        }.sortedBy {
            when (it) {
                is Constant -> 0
                is XPow -> 1
                is Pow -> 2
                is ExpX -> 3
                is SinX -> 4
                is CosX -> 5
                else -> 6
            }
        }
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

data class Pow<T>(val base: Expr<T>, val pow: Int) : Expr<T> {
    override fun render(): String {
        return when {
            base.isSimple -> """${base.render()}^$pow"""
            base is CosX -> """\cos^$pow x"""
            base is SinX -> """\sin^$pow x"""
            else -> """\left(${base.render()}\right)^$pow"""
        }
    }
}

private val xPowMap = HashMap<Integer, XPow>()

fun xPow(pow: Expr<Nothing>): Expr<Any?> {
    if (pow == Zero) return One
    if (pow is Integer) xPowMap[pow] ?: XPow(pow).also { xPowMap[pow] = it }
    return XPow(pow)
}

data class XPow(val pow: Expr<Nothing>) : Expr<Any?> {
    override fun render(): String {
        if (pow == One) {
            return "x"
        }
        return """x^${pow.render()}"""
    }
}

data class Divide<T>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T> {
    override fun render(): String =
        """\frac{${numerator.render()}}{${denominator.render()}}"""
}

data class Differentiate(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String = """\frac{d}{dx}\left(${base.render()}\right)"""
}

data class Integrate(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String = """\int{${base.render()}}"""
}

data class Sqrt<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\sqrt{${base.render()}}"""
}

data class Log<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\log\left(${base.render()}\right)"""
}

data class Invert(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String {
        TODO("Not yet implemented")
    }

}
