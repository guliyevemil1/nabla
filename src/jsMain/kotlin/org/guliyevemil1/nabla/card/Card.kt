package org.guliyevemil1.nabla.card

import org.guliyevemil1.nabla.math.CosX
import org.guliyevemil1.nabla.math.ExpX
import org.guliyevemil1.nabla.math.Expr
import org.guliyevemil1.nabla.math.Limit
import org.guliyevemil1.nabla.math.SinX
import org.guliyevemil1.nabla.math.X
import org.guliyevemil1.nabla.math.X2
import org.guliyevemil1.nabla.math.differentiate
import org.guliyevemil1.nabla.math.divide
import org.guliyevemil1.nabla.math.integer
import org.guliyevemil1.nabla.math.integrate
import org.guliyevemil1.nabla.math.lim
import org.guliyevemil1.nabla.math.log
import org.guliyevemil1.nabla.math.multiply
import org.guliyevemil1.nabla.math.pow
import org.guliyevemil1.nabla.math.sqrt

private val NablaCardOrdering = listOf(
    CardZero,
    CardOne,
    CardX,
    CardX2,
    CardExpX,
    CardSinX,
    CardCosX,
    Over,
    Times,
    DDx,
    Integrate,
    Inverse,
    Lim0,
    LimInf,
    LimInfimum,
    LimNegInf,
    LimSupremum,
    Log,
    Sqrt,
    Nabla,
    Nabla2,
).mapIndexed { index, card -> card to index }.toMap()

val NablaCardComparator: Comparator<NablaCard> = compareBy { NablaCardOrdering[it] }

sealed interface NablaCard : Card {
    fun render(): String
}

sealed class BaseCard(val expr: Expr<Any?>) : NablaCard {
    override fun render(): String = expr.render()
}

object CardZero : BaseCard(integer(0))
object CardOne : BaseCard(integer(1))
object CardX : BaseCard(X)
object CardX2 : BaseCard(X2)
object CardExpX : BaseCard(ExpX)
object CardSinX : BaseCard(SinX)
object CardCosX : BaseCard(CosX)

sealed interface AllOperator : NablaCard {
    fun transformExpr(expr: Expr<Any?>): Expr<Any?>
}

object Nabla : AllOperator {
    override fun render(): String = """\nabla"""

    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        differentiate(expr)
}

object Nabla2 : AllOperator {
    override fun render(): String = """\triangle"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        differentiate(differentiate(expr))
}

sealed interface Operator : NablaCard {
    fun transformExpr(expr: Expr<Any?>): Expr<Any?>
}

object DDx : Operator {
    override fun render(): String = """\frac{d}{dx}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> = differentiate(expr)
}

object Integrate : Operator {
    override fun render(): String = """\int"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> = integrate(expr)
}

sealed interface BinaryOperator : NablaCard {
    fun transformExpr(expr: Expr<Any?>, rhs: Expr<Any?>): Expr<Any?>
}

object Times : BinaryOperator {
    override fun render(): String = """\times"""
    override fun transformExpr(
        expr: Expr<Any?>,
        rhs: Expr<Any?>,
    ): Expr<Any?> = multiply(expr, rhs)
}

object Over : BinaryOperator {
    override fun render(): String = """\div"""
    override fun transformExpr(
        expr: Expr<Any?>,
        rhs: Expr<Any?>,
    ): Expr<Any?> = divide(expr, rhs)
}

object Lim0 : Operator {
    override fun render(): String = """\displaystyle\lim_{x \to 0}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        lim(expr, Limit.Zero)
}

object LimInf : Operator {
    override fun render(): String = """\displaystyle\lim_{x \to \infty}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        lim(expr, Limit.Infinity)
}

object LimNegInf : Operator {
    override fun render(): String = """\displaystyle\lim_{x \to -\infty}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        lim(expr, Limit.NegativeInfinity)
}

object LimSupremum : Operator {
    override fun render(): String = """\displaystyle\limsup_{x \to 0}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        lim(expr, Limit.Supremum)
}

object LimInfimum : Operator {
    override fun render(): String = """\displaystyle\liminf_{x \to 0}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> =
        lim(expr, Limit.Infimum)
}

object Sqrt : Operator {
    override fun render(): String = """\sqrt{}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> = sqrt(expr)
}

object Log : Operator {
    override fun render(): String = """\log"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> = log(expr)
}

object Inverse : Operator {
    override fun render(): String = """f^{-1}"""
    override fun transformExpr(expr: Expr<Any?>): Expr<Any?> {
        TODO("Not yet implemented")
    }
}

object NablaDeck : Deck<NablaCard> {

    override val cards: List<NablaCard> = buildList {
        repeat(times = 2) { add(CardZero) }
        repeat(times = 2) { add(CardOne) }
        repeat(times = 8) { add(CardX) }
        repeat(times = 3) { add(CardX2) }
        repeat(times = 4) { add(CardSinX) }
        repeat(times = 4) { add(CardCosX) }
        repeat(times = 4) { add(CardExpX) }

        repeat(times = 10) { add(Nabla) }
        repeat(times = 2) { add(Nabla2) }

        repeat(times = 8) { add(Integrate) }
        repeat(times = 8) { add(DDx) }

//        repeat(times = 2) { add(Inverse) }
        repeat(times = 2) { add(Sqrt) }
        repeat(times = 3) { add(Log) }

        repeat(times = 5) { add(Times) }
        repeat(times = 5) { add(Over) }

        repeat(times = 2) { add(Lim0) }
        repeat(times = 2) { add(LimInf) }
        repeat(times = 2) { add(LimNegInf) }
        add(LimSupremum)
        add(LimInfimum)
    }
}
