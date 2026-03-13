package org.guliyevemil1.nabla

sealed interface Expr<out T>

object Illegal : Expr<Nothing>

class Add<T : Base>(s: List<Expr<T>>) : Expr<T> {
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

    fun <U : Base> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        add(summands.map(f))
}

class Multiply<T : Base>(m: List<Expr<T>>) : Expr<T> {
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

    fun <U : Base> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        multiply(multiplicants.map(f))
}

data class Pow<T>(val base: Expr<T>, val pow: Int) : Expr<T>

data class Divide<T : Base>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T>

data class Differentiate(val base: Expr<Base>) : Expr<Base>

data class Integrate(val base: Expr<Base>) : Expr<Base>

data class Sqrt<T>(val base: Expr<T>) : Expr<T>

data class Log<T>(val base: Expr<T>) : Expr<T>

data class Invert<T>(val base: Expr<T>) : Expr<T>
