package org.guliyevemil1.nabla.math

fun differentiate(m: Multiply<Any?>): Expr<Any?> = add(
    List(m.multiplicants.size) { index ->
        val mm = m.multiplicants.toMutableList()
        mm[index] = differentiate(mm[index])
        multiply(mm)
    }
)

fun differentiate(b: Expr<Any?>): Expr<Any?> =
    when (b) {
        is Illegal -> Illegal
        is Constant -> Zero
        is X -> One
        is SinX -> CosX
        is CosX -> multiply(NegOne, SinX)
        is ExpX -> ExpX

        is Add -> b.map { differentiate(it) }
        is Multiply -> differentiate(b)

        is Divide<*> -> divide(
            add(
                multiply(
                    differentiate(b.numerator),
                    b.denominator,
                ),
                Scale(
                    NegOne,
                    multiply(
                        b.numerator,
                        differentiate(b.denominator),
                    )
                ),
            ),
            multiply(b.denominator, b.denominator)
        )

        is Differentiate -> Differentiate(b)
        is Integrate -> b.base
        is Invert -> TODO()
        is Log -> divide(differentiate(b.base), b.base)
        is Sqrt -> divide(differentiate(b.base), Scale(integer(2), b))
        is Pow -> {
            multiply(Scale(integer(b.pow), b.base), differentiate(b.base))
        }

        is XPow -> {
            multiply(b.pow, XPow(add(b.pow, NegOne)))
        }

        is Scale -> {
            multiply(b.factor, differentiate(b.expr))
        }
    }
