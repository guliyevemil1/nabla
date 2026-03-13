package org.guliyevemil1.nabla

sealed interface Expr<out T>

class Add<T>(val summands: List<Expr<T>>) : Expr<T> {
    constructor(vararg summands: Expr<T>) : this(summands.asList())

    fun <U> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        add(summands.map(f))
}

open class Multiply<T>(val multiplicants: List<Expr<T>>) : Expr<T> {
    constructor(vararg multiplicants: Expr<T>) : this(multiplicants.asList())

    fun <U> map(f: (Expr<T>) -> Expr<U>): Expr<U> =
        multiply(multiplicants.map(f))
}

data class Pow<T>(val base: Expr<T>, val pow: Int) : Expr<T>

data class Divide<T>(val numerator: Expr<T>, val denominator: Expr<T>) : Expr<T>

data class Differentiate<T>(val base: Expr<T>) : Expr<T>

data class Integrate<T : Expr<T>>(val base: Expr<T>) : Expr<Base<T>>

data class Sqrt<T>(val base: Expr<T>) : Expr<T>

data class Log<T>(val base: Expr<T>) : Expr<T>

data class Invert<T>(val base: Expr<T>) : Expr<T>
