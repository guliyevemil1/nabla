package org.guliyevemil1.nabla.math

val X = xPow(One)
val X2 = xPow(integer(2))

object CosX : Expr<Any?> {
    override val isSimple = true
    override fun render(): String = """\cos(x)"""
    override fun toLisp(): String = "(cos x)"
}

object SinX : Expr<Any?> {
    override val isSimple = true
    override fun render(): String = """\sin(x)"""
    override fun toLisp(): String = "(sin x)"
}

object ExpX : Expr<Any?> {
    override val isSimple = true
    override fun render(): String = """e^x"""
    override fun toLisp(): String = "(exp x)"
}
