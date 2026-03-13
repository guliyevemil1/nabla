package org.guliyevemil1.nabla

fun differentiate(m: Multiply): Base = add(
    List(m.multiplicants.size) { index ->
        val mm = m.multiplicants.toMutableList()
        mm[index] = differentiate(mm[index])
        multiply(mm)
    }
)

fun differentiate(b: Base): Base =
    when (b) {
        is Illegal -> Illegal
        is Constant -> Zero
        is X -> One
        is SinX -> CosX
        is CosX -> Multiply(NegOne, SinX)
        is ExpX -> ExpX

        is Add -> b.map { differentiate(it) }
        is Multiply -> differentiate(b)

        is Divide -> Divide(
            add(
                multiply(
                    differentiate(b.numerator),
                    b.denominator,
                ),
                multiply(
                    NegOne,
                    b.numerator,
                    differentiate(b.denominator),
                ),
            ),
            multiply(b.denominator, b.denominator)
        )

        is Differentiate -> Differentiate(b)
        is Integrate -> b.base
        is Invert -> TODO()
        is Log -> Divide(differentiate(b.base), b.base)
        is Sqrt -> Divide(differentiate(b.base), Multiply(Two, b))
    }
