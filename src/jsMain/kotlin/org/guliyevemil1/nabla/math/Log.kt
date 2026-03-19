package org.guliyevemil1.nabla.math

fun <T> log(c: Expr<T>): Expr<T> {
    if (c is Constant) {
        return Zero
    }
    if (c is ExpX) {
        return X as Expr<T>
    }
    if (c is X) {
        return Log(X) as Expr<T>
    }
    if (c is XPow) {
        return multiply(c.pow, Log(X)) as Expr<T>
    }
    return Log(c)
}
