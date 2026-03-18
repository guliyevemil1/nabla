package org.guliyevemil1.nabla.math

fun <T> sqrt(c: Expr<T>): Expr<T> {
    if (c is Constant) {
        if (c == Zero || c == One) return c
        return Sqrt(c)
    }
    if (c is Pow<*> && c.base is X && c.pow % 2 == 0) {
        if (c.pow == 2) {
            return X as Expr<T>
        }
        return pow(X, c.pow / 2) as Expr<T>
    }
    if (c is Multiply && c.multiplicants.size == 2 && c.multiplicants[0] is X && c.multiplicants[1] is X) {
        return X as Expr<T>
    }
    return Sqrt(c)
}
