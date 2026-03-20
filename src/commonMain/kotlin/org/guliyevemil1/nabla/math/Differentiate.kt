package org.guliyevemil1.nabla.math

data class Differentiate(val base: Expr<Any?>) : Expr<Any?> {
    override fun render(): String = """\frac{d}{dx}\left(${base.render()}\right)"""
    override fun toLisp(): String = "(ddx ${base.toLisp()})"
}

fun differentiate(m: Multiply<Any?>): Expr<Any?> = add(
    List(m.multiplicants.size) { index ->
        val mm = m.multiplicants.toMutableList()
        mm[index] = differentiate(mm[index])
        multiply(mm)
    }
)

fun differentiate(b: Expr<Any?>): Expr<Any?> =
    when (b) {
        is Bottom -> Bottom
        is Constant -> Zero
        is SinX -> CosX
        is CosX -> negate(SinX)
        is ExpX -> ExpX
        is XPow -> {
            multiply(b.pow, xPow(add(b.pow, NegOne)))
        }

        is Add -> b.map { differentiate(it) }
        is Multiply -> differentiate(b)

        is Divide<*> -> {
            val f = b.numerator
            val g = b.denominator
            val df = differentiate(f)
            val dg = differentiate(g)
            val gdf = multiply(df, g)
            val fdg = multiply(f, dg)
            val n = add(gdf, negate(fdg))
            val d = multiply(g, g)
            divide(n, d)
        }

        is Differentiate -> Differentiate(b)
        is Integrate -> b.base
        is Invert -> TODO()
        is Log -> divide(differentiate(b.base), b.base)
        is Sqrt -> divide(differentiate(b.base), Scale(integer(2), b))
        is Pow -> {
            multiply(multiply(integer(b.pow), b.base), differentiate(b.base))
        }

        is Scale -> {
            multiply(b.factor, differentiate(b.expr))
        }
    }
