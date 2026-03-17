package org.guliyevemil1.nabla

fun <T> sqrt(c: Expr<T>): Expr<T> {
    if (c is Constant) {
        TODO()
    }
    if (c is Multiply && c.multiplicants.size == 2 && c.multiplicants[0] is X && c.multiplicants[1] is X) {
        return X as Expr<T>
    }
    return Log(c)
}
