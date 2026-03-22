package org.guliyevemil1.nabla.math

data class Scale(val factor: Expr<Nothing>, val expr: Expr<Any?>) : Expr<Any?> {
    override val isConstant: Boolean = expr.isConstant

    override fun render(): String =
        if (factor == integer(-1)) {
            """-${expr.render()}"""
        } else if (expr.isSimple) {
            """${factor.render()} ${expr.render()}"""
        } else {
            """${factor.render()} \left(${expr.render()}\right)"""
        }

    override fun toLisp(): String = buildString {
        append("(scale ${factor.toLisp()} ${expr.toLisp()})")
    }
}

fun <T> scale(factor: Expr<Nothing>, expr: Expr<T>): Expr<T> =
    when {
        factor == Bottom -> Bottom
        factor == Zero -> Zero
        factor == One -> expr
//        factor is Scale -> scale(multiply(factor.factor, factor.expr as Expr<Nothing>), expr)

        expr is Add -> expr.map { scale(factor, it) }

        expr is Integral ->
            multiply(factor, expr)

        expr is Scale ->
            scale(multiply(factor, expr.factor), expr.expr) as Expr<T>

        expr is Rational -> divide(
            multiply(
                factor,
                integer(expr.numerator)
            ),
            integer(expr.denominator)
        )

        expr is Divide && expr.numerator == One -> divide(factor, expr.denominator)

        else -> Scale(factor, expr) as Expr<T>
    }
