package org.guliyevemil1.nabla

sealed interface Expr<out T> {
    fun render(): String = "TODO()"
}

object Illegal : Expr<Nothing>

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
                    append("""\times""")
                }
                append("""\left(""")
                append(m.render().trim())
                append("""\right)""")
            }
        }
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
        }
    }

    fun <U> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        multiply(multiplicants.map(f))

    override fun render(): String = buildString {
        if (multiplicants.size == 1) {
            multiplicants[0].render()
        } else {
            multiplicants.forEachIndexed { index, m ->
                if (index > 0) {
                    append("""\times""")
                }
                append("""\left(""")
                append(m.render().trim())
                append("""\right)""")
            }
        }
    }
}

data class Pow<T>(val base: Expr<T>, val pow: Int) : Expr<T> {
    override fun render(): String =
        """\left(${base.render()}\right)^$pow"""
}

data class Divide<T>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T> {
    override fun render(): String =
        """\frac{${numerator.render()}}{${numerator.render()}}"""
}

data class Differentiate(val base: Expr<Any?>) : Expr<Any?>

data class Integrate(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String = """\int{${base.render()}}"""
}

data class Sqrt<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\sqrt{${base.render()}}"""
}

data class Log<T>(val base: Expr<T>) : Expr<T> {
    override fun render(): String = """\log\left(${base.render()}\right)"""
}

data class Invert(val base: Expr<Any?>) : Expr<Any?>
