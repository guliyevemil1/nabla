package org.guliyevemil1.nabla

fun <T : Base> log(c: Expr<T>): Expr<T> {
    if (c is Constant) {
        TODO()
    }
    if (c is ExpX) {
        return X as Expr<T>
    }
    return Log(c)
}
