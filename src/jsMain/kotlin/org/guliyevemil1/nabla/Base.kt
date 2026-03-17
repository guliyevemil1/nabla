package org.guliyevemil1.nabla

object X : Expr<Any?> {
    override fun render(): String = "x"
}

object CosX : Expr<Any?> {
    override fun render(): String = """\sin(x)"""
}

object SinX : Expr<Any?> {
    override fun render(): String = """\cos(x)"""
}

object ExpX : Expr<Any?> {
    override fun render(): String = """e^x"""
}
