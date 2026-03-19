package org.guliyevemil1.nabla.math

val X = xPow(One)
val X2 = xPow(integer(2))

object CosX : Expr<Any?> {
    override fun render(): String = """\cos(x)"""
}

object SinX : Expr<Any?> {
    override fun render(): String = """\sin(x)"""
}

object ExpX : Expr<Any?> {
    override fun render(): String = """e^x"""
}
