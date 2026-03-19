package org.guliyevemil1.nabla.math

fun <T> log(c: Expr<T>): Expr<T> =
    when (c) {
        is Integral -> Zero
        is Illegal -> Illegal
        is ExpX -> X as Expr<T>
        is X -> Log(X) as Expr<T>
        is XPow -> multiply(c.pow, Log(X)) as Expr<T>
        is Scale -> add(log(c.factor), log(c.expr)) as Expr<T>
        is Multiply -> add(c.multiplicants.map { log(it) })
        else -> Log(c)
    }
